package com.mist.cloud.module.share.service;

import com.mist.cloud.core.exception.ShareException;
import com.mist.cloud.core.utils.Session;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.infrastructure.entity.Folder;
import com.mist.cloud.infrastructure.entity.Share;
import com.mist.cloud.infrastructure.entity.User;
import com.mist.cloud.module.file.context.IFileContext;
import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.module.share.model.ShareFileInfo;
import com.mist.cloud.module.share.model.ShareItem;
import com.mist.cloud.module.share.model.ShareStatusType;
import com.mist.cloud.module.share.model.req.CreateShareRequest;
import com.mist.cloud.module.share.model.resp.ShareLinkResponse;
import com.mist.cloud.module.share.repository.IShareRepository;
import com.mist.cloud.module.share.service.support.ShareCommonSupport;
import com.mist.cloud.module.user.repository.IUserRepository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 09:43
 * @Description:
 */
public abstract class AbstractShareContext extends ShareCommonSupport implements IShareContext {
    @Resource
    protected IShareRepository shareRepository;
    @Resource
    protected IFileRepository fileRepository;
    @Resource
    protected IFolderRepository folderRepository;
    @Resource
    protected IUserRepository userRepository;
    @Resource
    protected IFileContext fileContext;

    protected abstract ShareLinkResponse generateLink(String code);

    protected abstract String getCompleteUrl(String key);

    protected abstract Date parseExpireTime(Integer effectiveTime);

    protected abstract void resaveFolder(Long id, Long targetFolderId);


    @Override
    public ShareLinkResponse createShare(CreateShareRequest createShareRequest) {
        Long fileId = createShareRequest.getFileId();

        String code = createShareRequest.getCode();

        ShareLinkResponse shareLink = generateLink(code);
        Date expireTime = parseExpireTime(createShareRequest.getTimeLimit());
        Long userId = Session.getLoginId();

        Share share = Share.builder()
                .fileId(fileId)
                .createTime(new Date())
                .code(code)
                .expireTime(expireTime)
                .userId(userId)
                .visitLimit(createShareRequest.getVisitLimit())
                .expireTime(expireTime)
                .description(createShareRequest.getDescription())
                .uniqueKey(shareLink.getUniqueKey())
                .build();

        shareRepository.createShare(share);
        return shareLink;
    }

    @Override
    public void deleteShare(String identifier) {
        shareRepository.deleteShare(identifier);
    }

    @Override
    public void resave(List<Long> idList, Long targetFolderId) {
        for (Long id : idList) {
            // 文件夹转存直接复制就可以了
            if (!fileRepository.isFolder(id)) {
                fileContext.copy(id, targetFolderId);
                continue;
            }

            // 文件夹转存需要递归修改文件夹的userId
            resaveFolder(id, targetFolderId);
        }
    }

    @Override
    public List<ShareItem> listShares() {
        Long userId = Session.getLoginId();
        List<Share> shares = shareRepository.getAllShares(userId);

        List<ShareItem> shareItemList = shares.stream()
                .map(share -> {
                    ShareItem shareItem = new ShareItem();
                    Long fileId = share.getFileId();
                    shareItem.setFileId(fileId);
                    shareItem.setUrl(getCompleteUrl(share.getUniqueKey()));
                    shareItem.setDescription(share.getDescription());
                    shareItem.setCode(share.getCode());
                    shareItem.setVisitTime(100); // TODO 链接的查看次数

                    if (fileRepository.isFolder(fileId)) {
                        Folder folder = folderRepository.findFolder(fileId);
                        shareItem.setName(folder.getName());
                        shareItem.setIsFolder(true);
                    } else {
                        File file = fileRepository.findFile(fileId);
                        shareItem.setName(file.getName());
                        shareItem.setIsFolder(false);
                    }
                    return shareItem;
                })
                .collect(Collectors.toList());

        return shareItemList;
    }

    @Override
    public ShareFileInfo extractFile(String code, String identifier) {
        Share share = shareRepository.getShare(identifier);
        if (share == null) {
            throw new ShareException("页面不存在");
        }

        if (!share.getCode()
                .equals(code)) {
            throw new ShareException("提取码错误");
        }

        User user = userRepository.getUser(share.getUserId());

        ShareFileInfo shareFileInfo = new ShareFileInfo();
        shareFileInfo.setUser(new ShareFileInfo.User(user.getId(), user.getUsername()));
        shareFileInfo.setDescription(share.getDescription());
        shareFileInfo.setLink(getCompleteUrl(share.getUniqueKey()));
        shareFileInfo.setCreateTime(share.getCreateTime());
        shareFileInfo.setExpireTime(share.getExpireTime());
        shareFileInfo.setStatus(ShareStatusType.OK);

        Long id = share.getFileId();

        shareFileInfo.setFileId(id);
        if (fileRepository.isFolder(id)) {
            Folder folder = folderRepository.findFolder(id);
            shareFileInfo.setFileName(folder.getName());
            shareFileInfo.setIsFolder(true);
        } else {
            File file = fileRepository.findFile(id);
            shareFileInfo.setFileName(file.getName());
            shareFileInfo.setIsFolder(false);
        }

        if (share.getExpireTime() == null) {
            shareFileInfo.setStatus(ShareStatusType.PERMANENT);
        } else if (new Date().after(share.getExpireTime())) {
            shareFileInfo.setStatus(ShareStatusType.EXPIRED);
        }


        shareFileInfo.setStatus(ShareStatusType.OK);

        return shareFileInfo;
    }

}
