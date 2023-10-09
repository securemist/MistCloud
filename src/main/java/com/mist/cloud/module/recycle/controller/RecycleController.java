package com.mist.cloud.module.recycle.controller;

import com.mist.cloud.core.result.R;
import com.mist.cloud.module.recycle.model.RecycleFile;
import com.mist.cloud.module.recycle.service.RecycleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.util.List;

import static com.mist.cloud.core.utils.Session.getLoginId;

/**
 * @Author: securemist
 * @Datetime: 2023/9/17 08:49
 * @Description:
 */
@RestController
@Tag(name = "回收站")
public class RecycleController {
    @Resource
    RecycleService recycleService;

    @GetMapping("/recycle/list")
    @Operation(summary = "列出回收站所有内容")
    public R recycleList() {
        Long userId = getLoginId();
        List<RecycleFile> list =  recycleService.listAll(userId);
        return R.success(list);
    }

    @GetMapping("/recycle/restore")
    @Operation(summary = "从回收站还原文件")
    public R restoreFile(@RequestParam Long id) {
        recycleService.restoreFile(id);
        return R.success();
    }

    @GetMapping("/recycle/delete")
    @Operation(summary = "永久删除文件")
    public R delete(@RequestParam Long id) {
        Long userId = getLoginId();
        recycleService.deleteFile( id);
        return R.success();
    }

    @GetMapping("/recycle/clearAll")
    @Operation(summary = "清空回收站")
    public R clear() {
        Long userId = getLoginId();
        recycleService.clearRecycle(userId);
        return R.success();
    }

}
