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

    Share selectShareByIdentifier(String unique_key);

    void deleteShareByIdentifier(String unique_key);

    List<Share> selectAllSharesByUserId(Long userId);

    void increaseVisitTimesByUniqueKey(String uniqueKey);

    void increaseDownloadTimesByUniqueKey(String uniqueKey);
}
