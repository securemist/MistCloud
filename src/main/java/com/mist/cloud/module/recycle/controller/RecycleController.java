package com.mist.cloud.module.recycle.controller;

import com.mist.cloud.core.result.R;
import com.mist.cloud.module.recycle.model.RecycleFile;
import com.mist.cloud.module.recycle.service.RecycleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.util.List;

import static com.mist.cloud.core.utils.Session.getLoginId;

/**
 * @Author: securemist
 * @Datetime: 2023/9/17 08:49
 * @Description:
 */
@RestController
public class RecycleController {
    @Resource
    RecycleService recycleService;

    // 列出回收站所有内容
    @GetMapping("/recycle/list")
    public R recycleList() {
        Long userId = getLoginId();
        List<RecycleFile> list =  recycleService.listAll(userId);
        return R.success(list);
    }

    // 还原文件
    @GetMapping("/recycle/restore")
    public R restoreFile(@RequestParam Long id) {
        recycleService.restoreFile(id);
        return R.success();
    }

    // 永久删除文件
    @GetMapping("/recycle/delete")
    public R delete(@RequestParam Long id) {
        Long userId = getLoginId();
        recycleService.deleteFile( id);
        return R.success();
    }

    // 清空回收站
    @GetMapping("/recycle/clearAll")
    public R clear() {
        Long userId = getLoginId();
        recycleService.clearRecycle(userId);
        return R.success();
    }

}
