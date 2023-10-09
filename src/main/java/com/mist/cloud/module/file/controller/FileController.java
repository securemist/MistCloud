package com.mist.cloud.module.file.controller;

import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.core.exception.file.FolderException;
import com.mist.cloud.core.result.R;
import com.mist.cloud.module.file.context.FileServiceContext;
import com.mist.cloud.module.file.model.pojo.FolderDetail;
import com.mist.cloud.module.file.model.req.FileCopyRequest;
import com.mist.cloud.module.file.model.req.FileDeleteRequest;
import com.mist.cloud.module.file.model.tree.FolderTreeNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Lang;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/8/25 19:02
 * @Description:
 */
@RestController
@Tag(name = "文件(夹)操作")
public class FileController {
    @Resource
    private FileConfig fileConfig;
    @Resource
    private FileServiceContext fileServiceContext;

    @GetMapping(value = "/folder/tree")
    @Operation(summary = "返回所有文件的文件树")
    public R folderTree() {
        // 查找的文件夹 id，默认为根目录
        FolderTreeNode tree = fileServiceContext.getFolderTree();
        return R.success(tree);
    }

    @GetMapping("/folder/create")
    @Operation(summary = "创建文件夹")
    @Parameters({
            @Parameter(name = "parentId", description = "创建文件夹时所在的文件夹 Id"),
            @Parameter(name = "folderName", description = "文件夹的名字")})
    public R createFolder(@RequestParam("parentId") Long parentId, @RequestParam("folderName") String folderName) throws FolderException {
        fileServiceContext.createFolder(parentId, folderName);
        return R.success();
    }

    @GetMapping("/folder/{id}")
    @Operation(summary = "获取一个文件夹下所有的文件与文件夹信息")
    @Parameter(name = "id", description = "文件夹 Id")
    public R getFiles(@PathVariable("id") Long id) {
        FolderDetail folderDetail = fileServiceContext.getFolderDetail(id);
        return R.success(folderDetail);
    }


    @GetMapping(value = "/file/search")
    @Operation(summary = "文件搜索")
    @Parameters({
            @Parameter(name = "value", description = "搜索关键词")})
    public R rename(@RequestParam("value") String value) {
        // FIXME 多用户的搜索会出现冲突
        FolderDetail folderDetail = fileServiceContext.searchFile(value);
        return R.success(folderDetail);
    }


    @GetMapping(value = "/file/rename")
    @Operation(summary = "文件重命名")
    @Parameters({
            @Parameter(name = "id", description = "要修改的文件(夹)名 id"),
            @Parameter(name = "name", description = "修改后的文件(夹)名")})
    public R rename(@RequestParam("id") Long id, @RequestParam("name") String fileName) {
        fileServiceContext.rename(id, fileName);
        return R.success();
    }


    @PostMapping(value = "/file/copy")
    @Operation(summary = "文件复制")
    @Parameters({
            @Parameter(name = "targetFolderId", description = "目标文件夹 id"),
            @Parameter(name = "idList", description = "当前文件 id")})
    public R copy(@RequestBody FileCopyRequest fileCopyRequest) {
        for (Long id : fileCopyRequest.getIdList()) {
            fileServiceContext.copy(id, fileCopyRequest.getTargetFolderId());
        }
        return R.success();
    }

    @PostMapping(value = "/file/delete")
    @Operation(summary = "文件删除")
    @Parameters({
            @Parameter(name = "idList", description = "要删除的文件 id集合"),
            @Parameter(name = "realDelete", description = "是否真实删除")
    })
    public R delete(@RequestBody FileDeleteRequest fileDeleteRequest) {
        for (Long id : fileDeleteRequest.getIdList()) {
            fileServiceContext.delete(id, fileDeleteRequest.getRealDelete());
        }
        return R.success();
    }
}
