package com.mist.cloud.infrastructure.repository;

import com.mist.cloud.infrastructure.entity.Share;
import com.mist.cloud.infrastructure.mapper.ShareMapper;
import com.mist.cloud.module.share.repository.IShareRepository;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 09:45
 * @Description:
 */
@Component
public class ShareRepository implements IShareRepository {
    @Resource
    private ShareMapper shareMapper;


    @Override
    public void createShare(Share share) {
        shareMapper.insertShare(share);
    }

    @Override
    public Share getShare(String uniqueKey) {
        return  shareMapper.selectShareByIdentifier(uniqueKey);
    }

    @Override
    public void deleteShare(String uniqueKey) {
        shareMapper.deleteShareByIdentifier(uniqueKey);
    }

    @Override
    public List<Share> getAllShares(Long userId) {
        return shareMapper.selectAllSharesByUserId(userId);
    }

    @Override
    public void updateVisitTimes(String uniqueKey) {
        shareMapper.increaseVisitTimesByUniqueKey(uniqueKey);
    }

    @Override
    public void updateDownloadTimes(String uniqueKey) {
        shareMapper.increaseDownloadTimesByUniqueKey(uniqueKey);
    }

}
