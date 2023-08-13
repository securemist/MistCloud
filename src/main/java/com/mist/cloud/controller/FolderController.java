package com.mist.cloud.controller;

import com.mist.cloud.common.Constants;
import com.mist.cloud.common.result.FailedResult;
import com.mist.cloud.common.result.Result;
import com.mist.cloud.common.result.SuccessResult;
import com.mist.cloud.model.dto.FolderDto;
import com.mist.cloud.model.dto.UserCapacityDto;
import com.mist.cloud.model.pojo.FolderDetail;
import com.mist.cloud.model.tree.FolderTreeNode;
import com.mist.cloud.model.tree.SubFolder;
import com.mist.cloud.service.IFolderService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;




/**
 * @Author: securemist
 * @Datetime: 2023/7/19 18:46
 * @Description:
 */
@RestController
@Slf4j
public class FolderController {

    @Resource
    private IFolderService folderService;

    @GetMapping("/folder/create")
    @ApiOperation(value = "创建文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "创建文件夹时所在的文件夹 Id"),
            @ApiImplicitParam(name = "folderName", value = "文件夹的名字")})
    public Result createFolder(@RequestParam("parentId") Long parentId, @RequestParam("folderName") String folderName) {
        FolderDto folderDto;
        folderDto = folderService.createFolder(parentId, folderName);
        return new SuccessResult(folderDto);
    }

    @GetMapping("/folder/{folderId}")
    @ApiOperation(value = "获取一个文件夹下所有的文件与文件夹")
    @ApiImplicitParam(name = "folderId", value = "文件夹 Id")
    public Result getFiles(@PathVariable("folderId") Long folderId) {

        FolderDetail folderDetail = folderService.getFiles(folderId);
        return new SuccessResult(folderDetail);
    }

    @GetMapping(value = "/capacity")
    @ApiOperation(value = "统计用户的云盘容量信息", notes = "返回的数据用 B 表示，前端转为 MB 或者 GB")
    public Object getCapacityInfo() {
        UserCapacityDto userCapacityDto = folderService.getCapacityInfo();
        return new SuccessResult(userCapacityDto);
    }

    @GetMapping(value = "/folder/rename")
    @ApiOperation(value = "文件夹重命名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "folderId", value = "要修改的文件夹 id"),
            @ApiImplicitParam(name = "folderName", value = "修改后的文件名")})
    public Result rename(@RequestParam("folderId") Long folderId, @RequestParam("folderName") String folderName) {
        folderService.rename(folderId, folderName);
        return new SuccessResult();
    }

    @GetMapping(value = "/folder/delete")
    @ApiOperation(value = "文件夹删除")
    @ApiImplicitParam(name = "folderId", value = "要修改的文件夹 id")
    public Result delete(@RequestParam("folderId") Long folderId) {
        folderService.deleteFolder(folderId);
        return new SuccessResult();
    }

    @GetMapping(value = "/folder/copy")
    @ApiOperation(value = "复制文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetFolderId", value = "目标文件夹 id"),
            @ApiImplicitParam(name = "folderId", value = "当前文件夹 id")})
    public Result copy(@RequestParam("targetFolderId") Long targetFolderId, @RequestParam("folderId") Long folderId) {
        folderService.copyFolder(folderId, targetFolderId);
        return new SuccessResult();
    }

    @GetMapping(value = "/folder/tree")
    @ApiOperation(value = "返回所有文件的文件树")
    public Result folderTree() {
        // 查找的文件夹 id，默认为根目录
        FolderTreeNode tree = folderService.getFolderTree();
        // 返回给前端数组形式，arr[0]=tree
        ArrayList<FolderTreeNode> arr = new ArrayList<>();
        arr.add(tree);
        return new SuccessResult(arr);
    }



}
