package com.mist.cloud.infrastructure.mapper;

import com.mist.cloud.infrastructure.entity.Share;
import com.mist.cloud.infrastructure.pojo.FolderResaveReq;
import org.apache.ibatis.annotations.Mapper;

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

}
