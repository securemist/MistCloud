package com.mist.cloud.service;

import com.mist.cloud.model.po.Chunk;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 08:58
 * @Description:
 */
public interface IChunkService {
    /**
     * 保存文件块
     *
     * @param chunk
     */
    void saveChunk(Chunk chunk);

    /**
     * 检查文件块是否存在
     *
     * @param identifier
     * @param chunkNumber
     * @return
     */
    boolean checkChunk(String identifier, Integer chunkNumber);
}
