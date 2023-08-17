package com.mist.cloud.config.context;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: securemist
 * @Datetime: 2023/8/16 16:21
 * @Description:
 */
@Component
public class UploadTaskContext {

    private ConcurrentMap<Long, String> map = new ConcurrentHashMap<>();

    public void set(String uid) {
        map.put(Thread.currentThread().getId(), uid);
    }

    public String get(){
       return map.remove(Thread.currentThread().getId());
    }

}
