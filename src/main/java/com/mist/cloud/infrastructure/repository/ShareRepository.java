package com.mist.cloud.infrastructure.repository;

import com.mist.cloud.infrastructure.entity.Share;
import com.mist.cloud.infrastructure.mapper.ShareMapper;
import com.mist.cloud.module.share.repository.IShareRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    public Share getShare(String identifier) {
        return  shareMapper.selectShareByIdentifier(identifier);
    }

    @Override
    public void deleteShare(String identifier) {
        shareMapper.deleteShareByIdentifier(identifier);
    }

    @Override
    public List<Share> getAllShares(Long userId) {
        return shareMapper.selectAllSharesByUserId(userId);
    }

}
