package com.mist.cloud.module.share.service;

import com.mist.cloud.module.share.model.ShareFileInfo;
import com.mist.cloud.module.share.model.ShareItem;
import com.mist.cloud.module.share.model.req.CreateShareRequest;
import com.mist.cloud.module.share.model.resp.ShareLinkResponse;

import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 09:38
 * @Description:
 */
public interface IShareContext {

    /**
     * 创建分享
     * @param createShareRequest
     * @return 返回分享链接
     */
    ShareLinkResponse createShare(CreateShareRequest createShareRequest);


    /**
     * 提取文件
     * @param code 提取码
     * @param identifier 分享的标识
     * @return
     */
    ShareFileInfo extractFile(String code, String uniqueKey);

    /**
     * 删除分享
     * @param identifier
     */
    void deleteShare(String uniqueKey);

    /**
     * 转存文件
     *
     * @param idList
     * @param targetFolderId
     */
    void resave(List<Long> idList, Long targetFolderId);

    /**
     * 列出用户创建的所有分享
     * @return
     */
    List<ShareItem> listShares();

}
