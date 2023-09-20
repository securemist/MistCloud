package com.mist.cloud.module.share.repository;

import com.mist.cloud.infrastructure.entity.Share;

import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 09:44
 * @Description:
 */
public interface IShareRepository {
    /**
     * 创建分享
     * @param share
     */
    void createShare(Share share);

    /**
     * 获取分享
     * @param identifier
     * @return
     */
    Share getShare(String identifier);

    /**
     * 删除分享
     * @param identifier
     */
    void deleteShare(String identifier);


    /**
     * 获取用户所有的分享
     *
     * @param userId
     * @return
     */
    List<Share> getAllShares(Long userId);
}
