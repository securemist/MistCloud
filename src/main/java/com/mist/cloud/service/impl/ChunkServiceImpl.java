package com.mist.cloud.service.impl;

import com.mist.cloud.config.context.UploadTaskContext;
import com.mist.cloud.model.po.Chunk;
import com.mist.cloud.model.vo.ChunkVo;
import com.mist.cloud.service.IChunkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @Author: securemist
 * @Datetime: 2023/8/17 08:59
 * @Description:
 */
@Service
public class ChunkServiceImpl implements IChunkService {



    @Override
    public void saveChunk(ChunkVo chunk) {
//        chunkRepository.save(chunk);
    }

    @Override
    public boolean checkChunk(String identifier, Integer chunkNumber) {
//        Specification<Chunk> specification = (Specification<Chunk>) (root, criteriaQuery, criteriaBuilder) -> {
//            List<Predicate> predicates = new ArrayList<>();
//            predicates.add(criteriaBuilder.equal(root.get("identifier"), identifier));
//            predicates.add(criteriaBuilder.equal(root.get("chunkNumber"), chunkNumber));
//
//            return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
//        };

return false;
//        return chunkRepository.findOne(specification).orElse(null) == null;
    }
}
