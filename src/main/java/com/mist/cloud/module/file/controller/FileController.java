package com.mist.cloud.module.file.controller;

import com.mist.cloud.core.config.FileConfig;
import com.mist.cloud.core.exception.file.FolderException;
import com.mist.cloud.core.result.R;
import com.mist.cloud.module.file.context.FileServiceContext;
import com.mist.cloud.module.file.model.pojo.FolderDetail;
import com.mist.cloud.module.file.model.req.FileCopyRequest;
import com.mist.cloud.module.file.model.req.FileDeleteRequest;
import com.mist.cloud.module.file.model.tree.FolderTreeNode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Lang;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/8/25 19:02
 * @Description:
 */
@RestController
public class FileController {
    @Resource
    private FileConfig fileConfig;
    @Resource
    private FileServiceContext fileServiceContext;

    @GetMapping(value = "/folder/tree")
    @ApiOperation(value = "返回所有文件的文件树")
    public R folderTree() {
        // 查找的文件夹 id，默认为根目录
        FolderTreeNode tree = fileServiceContext.getFolderTree();
        return R.success(tree);
    }

    @GetMapping("/folder/create")
    @ApiOperation(value = "创建文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "创建文件夹时所在的文件夹 Id", dataTypeClass = Lang.class),
            @ApiImplicitParam(name = "folderName", value = "文件夹的名字", dataTypeClass = String.class)})
    public R createFolder(@RequestParam("parentId") Long parentId, @RequestParam("folderName") String folderName) throws FolderException {
        fileServiceContext.createFolder(parentId, folderName);
        return R.success();
    }

    @GetMapping("/folder/{id}")
    @ApiOperation(value = "获取一个文件夹下所有的文件与文件夹信息")
    @ApiImplicitParam(name = "id", value = "文件夹 Id", dataTypeClass = Lang.class)
    public R getFiles(@PathVariable("id") Long id) {
        FolderDetail folderDetail = fileServiceContext.getFolderDetail(id);
        return R.success(folderDetail);
    }


    @GetMapping(value = "/file/search")
    @ApiOperation(value = "文件重命名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", value = "搜索关键词", dataTypeClass = String.class)})
    public R rename(@RequestParam("value") String value) {
        // FIXME 多用户的搜索会出现冲突
        FolderDetail folderDetail = fileServiceContext.searchFile(value);
        return R.success(folderDetail);
    }


    @GetMapping(value = "/file/rename")
    @ApiOperation(value = "文件重命名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要修改的文件(夹)名 id", dataTypeClass = Lang.class),
            @ApiImplicitParam(name = "name", value = "修改后的文件(夹)名", dataTypeClass = String.class)})
    public R rename(@RequestParam("id") Long id, @RequestParam("name") String fileName) {
        fileServiceContext.rename(id, fileName);
        return R.success();
    }


    @PostMapping(value = "/file/copy")
    @ApiOperation(value = "文件复制")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetFolderId", value = "目标文件夹 id", dataTypeClass = Lang.class),
            @ApiImplicitParam(name = "idList", value = "当前文件 id", dataTypeClass = Lang.class)})
    public R copy(@RequestBody FileCopyRequest fileCopyRequest) {
        for (Long id : fileCopyRequest.getIdList()) {
            fileServiceContext.copy(id, fileCopyRequest.getTargetFolderId());
        }
        return R.success();
    }

    @PostMapping(value = "/file/delete")
    @ApiOperation(value = "文件删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "要删除的文件 id集合", dataTypeClass = Lang.class),
            @ApiImplicitParam(name = "realDelete", value = "是否真实删除", dataTypeClass = Boolean.class)
    })
    public R delete(@RequestBody FileDeleteRequest fileDeleteRequest) {
        for (Long id : fileDeleteRequest.getIdList()) {
            fileServiceContext.delete(id, fileDeleteRequest.getRealDelete());
        }
        return R.success();
    }
}
