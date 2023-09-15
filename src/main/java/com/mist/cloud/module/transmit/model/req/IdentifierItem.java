package com.mist.cloud.module.transmit.model.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: securemist
 * @Datetime: 2023/9/15 21:44
 * @Description:
 */
@Data
public class IdentifierItem implements Serializable {
    String identifier;
    String md5;
}
