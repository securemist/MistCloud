package com.mist.cloud.infrastructure.mapper;

import com.mist.cloud.infrastructure.entity.Share;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 10:38
 * @Description:
 */
@Mapper
public interface ShareMapper {

    void insertShare(Share share);

    Share selectShareByIdentifier(String identifier);

    void deleteShareByIdentifier(String identifier);

    List<Share> selectAllSharesByUserId(Long userId);

}
