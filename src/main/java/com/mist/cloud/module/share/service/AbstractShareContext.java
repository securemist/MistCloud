package com.mist.cloud.module.share.service;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
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
import com.mist.cloud.module.share.model.ShareStatusType;
import com.mist.cloud.module.share.model.req.CreateShareRequest;
import com.mist.cloud.module.share.model.resp.ShareLinkResponse;
import com.mist.cloud.module.share.repository.IShareRepository;
import com.mist.cloud.module.share.service.support.ShareCommonSupport;
import com.mist.cloud.module.user.repository.IUserRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    protected abstract String generateUrl();

    protected abstract String getCompleteUrl(String uid);

    protected abstract Date parseExpireTime(Integer effectiveTime);

    protected abstract void resaveFolder(Long id, Long targetFolderId);


    @Override
    public ShareLinkResponse createShare(CreateShareRequest createShareRequest) {
        // 文件列表
        List<Long> idList = createShareRequest.getIdList();
        String fileIds = JSON.toJSONString(idList);

        String identifier = generateUrl();

        Date expireTime = parseExpireTime(createShareRequest.getEffectiveTime());

        String code = createShareRequest.getExtractCode();

        Long userId = Session.getLoginId();

        Share share = Share.builder()
                .fileIds(fileIds)
                .createTime(new Date())
                .extreactCode(code)
                .expireTime(expireTime)
                .userId(userId)
                .description(createShareRequest.getDescription())
                .identifier(identifier)
                .build();

        shareRepository.createShare(share);
        return new ShareLinkResponse(getCompleteUrl(share.getIdentifier()));
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
    public ShareFileInfo extractFile(String code, String identifier) {
        Share share = shareRepository.getShare(identifier);
        if (share == null) {
            throw new ShareException("页面不存在");
        }

        if (!share.getExtreactCode().equals(code)) {
            throw new ShareException("提取码错误");
        }

        User user = userRepository.getUser(share.getUserId());

        ShareFileInfo shareFileInfo = new ShareFileInfo();
        shareFileInfo.setUser(new ShareFileInfo.User(user.getId(), user.getUsername()));
        shareFileInfo.setDescription(share.getDescription());
        shareFileInfo.setLink(getCompleteUrl(share.getIdentifier()));
        shareFileInfo.setCreateTime(share.getCreateTime());
        shareFileInfo.setExpireTime(share.getExpireTime());
        shareFileInfo.setStatus(ShareStatusType.OK);

        if (share.getExpireTime() == null) {
            shareFileInfo.setStatus(ShareStatusType.PERMANENT);
        } else if (new Date().after(share.getExpireTime())) {
            shareFileInfo.setStatus(ShareStatusType.EXPIRED);
        }

        List<Long> idList = JSON.parseArray(share.getFileIds(), Long.class);
        ArrayList<ShareFileInfo.File> shareFileList = new ArrayList<>();
        for (Long id : idList) {
            ShareFileInfo.File shareFile = new ShareFileInfo.File();
            shareFile.setId(id);
            if (fileRepository.isFolder(id)) {
                Folder folder = folderRepository.findFolder(id);
                if (folder == null) {
                    shareFileInfo.setStatus(ShareStatusType.DELETED);
                    break;
                }
                shareFile.setSize(0L);
                shareFile.setName(folder.getName());
                shareFile.setModifyTime(folder.getModifyTime());
                shareFile.setIsFolder(true);
            } else {
                File file = fileRepository.findFile(id);
                if (file == null) {
                    shareFileInfo.setStatus(ShareStatusType.DELETED);
                    break;
                }
                shareFile.setSize(file.getSize());
                shareFile.setName(file.getName());
                shareFile.setModifyTime(file.getCreateTime());
                shareFile.setIsFolder(false);
            }
            shareFileList.add(shareFile);
        }
        shareFileInfo.setFileList(shareFileList);
        shareFileInfo.setStatus(ShareStatusType.OK);

        return shareFileInfo;
    }

}
