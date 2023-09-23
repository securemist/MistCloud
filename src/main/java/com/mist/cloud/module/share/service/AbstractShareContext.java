package com.mist.cloud.module.share.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.mist.cloud.core.exception.ShareException;
import com.mist.cloud.core.exception.ShareInvalidException;
import com.mist.cloud.core.utils.Session;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.infrastructure.entity.Folder;
import com.mist.cloud.infrastructure.entity.Share;
import com.mist.cloud.infrastructure.entity.User;
import com.mist.cloud.module.file.context.FileServiceContext;
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
    @Resource
    private FileServiceContext fileServiceContext;

    protected abstract ShareLinkResponse generateLink(String code);

    protected abstract String getCompleteUrl(String key, String code);

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
    public void deleteShare(String uniqueKey) {
        shareRepository.deleteShare(uniqueKey);
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
                    shareItem.setUrl(getCompleteUrl(share.getUniqueKey(), share.getCode()));
                    shareItem.setDescription(share.getDescription());
                    shareItem.setCode(share.getCode());
                    shareItem.setUniqueKey(share.getUniqueKey());
                    shareItem.setCreateTime(share.getCreateTime());
                    shareItem.setVisitTimes(share.getVisitTimes());
                    shareItem.setVisitLimit(share.getVisitLimit());
                    shareItem.setVisitTimes(share.getDownloadTimes());
                    shareItem.setExpireTime(share.getExpireTime());

                    try {
                        if (fileRepository.isFolder(fileId)) {
                            Folder folder = folderRepository.findFolder(fileId);
                            shareItem.setFileName(folder.getName());
                            shareItem.setIsFolder(true);
                        } else {
                            File file = fileRepository.findFile(fileId);
                            shareItem.setFileName(file.getName());
                            shareItem.setIsFolder(false);
                        }
                    } catch (Exception e) {
                        // 分享的文件已经是被完全删除了
                        shareItem.setIsFolder(false);
                        shareItem.setFileName("文件已被删除");
                    }

                    return shareItem;
                })
                .collect(Collectors.toList());

        return shareItemList;
    }

    @Override
    public ShareFileInfo extractFile(String code, String uniqueKey) throws ShareInvalidException {
        Share share = shareRepository.getShare(uniqueKey);
        checkExtractCode(share, code);

        // 访问次数达到限制
        checkVisitLimit(share);

        // 生成返回数据
        ShareFileInfo shareFileInfo = getShareFileInfo(share);

        // 访问次数+1
        shareRepository.updateVisitTimes(uniqueKey);

        return shareFileInfo;
    }


    /**
     * 校验提取码
     *
     * @param share share对象
     * @param code  用户输入的提取码
     */
    private void checkExtractCode(Share share, String code) {
        if (share == null) {
            throw new ShareInvalidException("分享已失效");
        }

        if (!share.getCode()
                .equals(code)) {
            throw new ShareException("提取码错误");
        }
    }

    /**
     * 校验访问次数是否达到限制
     *
     * @param share share对象
     */
    private void checkVisitLimit(Share share) {
        Long userId = 0L;
        try {
            userId = Session.getLoginId();
        } catch (NotLoginException e) {
        }

        // 排除分享者自身访问
        if (userId.equals(share.getUserId())) {
            return;
        }

        if (share.getVisitLimit() != 0 && share.getVisitTimes() == share.getVisitLimit()) {
            throw new ShareInvalidException("分享已失效");
        }
    }


    /**
     * 生成返回数据
     *
     * @param share
     * @return
     */
    private ShareFileInfo getShareFileInfo(Share share) {
        User user = userRepository.getUser(share.getUserId());

        ShareFileInfo shareFileInfo = new ShareFileInfo();
        shareFileInfo.setUser(new ShareFileInfo.User(user.getId(), user.getUsername()));
        shareFileInfo.setDescription(share.getDescription());
        shareFileInfo.setUrl(getCompleteUrl(share.getUniqueKey(), share.getCode()));
        shareFileInfo.setCreateTime(share.getCreateTime());
        shareFileInfo.setExpireTime(share.getExpireTime());
        shareFileInfo.setStatus(ShareStatusType.OK);
        Long fileId = share.getFileId();

        // 文件已被删除
        try {
            shareFileInfo.setFileId(fileId);
            if (fileRepository.isFolder(fileId)) {
                Folder folder = folderRepository.findFolder(fileId);
                shareFileInfo.setFileName(folder.getName());
                shareFileInfo.setIsFolder(true);
                shareFileInfo.setFolderDetail(fileServiceContext.getFolderDetail(fileId));
            } else {
                File file = fileRepository.findFile(fileId);
                shareFileInfo.setFileName(file.getName());
                shareFileInfo.setIsFolder(false);
            }
        } catch (Exception e) {
            throw new ShareInvalidException("分享已失效");
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
