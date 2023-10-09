package com.mist.cloud.module.file.context;

import com.mist.cloud.core.constant.Constants;
import com.mist.cloud.core.exception.RequestParmException;
import com.mist.cloud.core.exception.file.FolderException;
import com.mist.cloud.core.utils.DateTimeUtils;
import com.mist.cloud.core.utils.Session;
import com.mist.cloud.module.file.model.dto.FolderDto;
import com.mist.cloud.module.file.model.pojo.FolderDetail;
import com.mist.cloud.module.file.model.tree.FolderTreeNode;
import com.mist.cloud.module.file.model.tree.SubFolder;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.infrastructure.entity.Folder;
import com.mist.cloud.module.file.context.service.ICommonService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.dev33.satoken.stp.StpUtil.*;

/**
 * @Author: securemist
 * @Datetime: 2023/8/25 19:20
 * @Description: 文件与文件夹处理的主要逻辑
 * <p>
 * 文件与文件夹的CRUD逻辑非常相似，这里运用策略模式处理。
 * get(id) 根据id判断是文件操作还是文件夹操，返回对应的service去处理
 * <p>
 * 其他的接口直接在本类处理
 */
@Component
public class FileServiceContext extends AbstractFileServiceSupport implements IFileContext {
    @Resource(name = "fileService")
    public ICommonService fileService;
    @Resource(name = "folderService")
    public ICommonService folderService;

    @Override
    public ICommonService getService(Long id) {
        if (fileRepository.isFolder(id)) {
            return folderService;
        } else {
            return fileService;
        }
    }

    @Override
    public FolderTreeNode getFolderTree() {
        Long userId = Long.parseLong(getLoginId().toString());
        List<Folder> folderList = folderRepository.getFolderTree(userId);

        List<SubFolder> folderTreeVoList = folderList.stream()
                .map(folder -> {
                    SubFolder subFolder = SubFolder.builder()
                            .id(folder.getId())
                            .name(folder.getName())
                            .parentId(folder.getParentId())
                            .build();
                    return subFolder;
                }).collect(Collectors.toList());

        // 递归构造树形结构
        FolderTreeNode root = new FolderTreeNode(folderTreeVoList.get(0).getId(), folderTreeVoList.remove(0).getName());
        recur(folderTreeVoList, root);

        return root;
    }


    @Override
    public FolderDto createFolder(Long parentId, String folderName) throws FolderException {

        Folder folder = folderRepository.findFolder(parentId);
        if (folder == null) {
            throw new RequestParmException();
        }

        // 查看当前目录下同名文件夹是否存在
        List<Folder> subFolders = folderRepository.findSubFolders(parentId);
        for (Folder subFolder : subFolders) {
            if (subFolder.getName().equals(folderName)) { // 出现重名
                throw new FolderException("文件夹已存在");
            }
        }

        // 创建文件夹,返回创建之后的主键 ID
        Long id = folderRepository.createFolder(folderName, parentId);

        FolderDto folderDto = FolderDto.builder()
                .userId(Session.getLoginId())
                .id(id)
                .name(folderName)
                .parentId(parentId)
                .createTime(DateTimeUtils.now())
                .size(0L).build();

        return folderDto;
    }


    @Override
    public FolderDetail searchFile(String value) {
        List<File> fileList = fileRepository.searchByName(value);
        List<Folder> folderList = folderRepository.searchByName(value);

        // 返回给前端的数据不区分文件和文件夹，这里合并到一个集合中去
        ArrayList<FolderDetail.File> fileVoList = new ArrayList<>();
        for (Folder folder : folderList) {
            FolderDetail.File file = FolderDetail.File.builder()
                    .id(folder.getId())
                    .path(folderService.getPath(folder.getId()))
                    .name(folder.getName())
                    .modifyTime(folder.getModifyTime())
                    .isFolder(true)
                    .size(0L)
                    .build();
            fileVoList.add(file);
        }

        for (File file : fileList) {
            FolderDetail.File file0 = FolderDetail.File.builder()
                    .id(file.getId())
                    .name(file.getName())
                    .path(fileService.getPath(file.getId()))
                    .size(file.getSize())
                    .modifyTime(file.getCreateTime())
                    .isFolder(false)
                    .build();
            fileVoList.add(file0);
        }

        FolderDetail folderDetail = FolderDetail.builder()
                .path(new ArrayList<>())
                .name("")
                .fileList(fileVoList)
                .build();

        return folderDetail;
    }


    @Override
    public FolderDetail getFolderDetail(Long id) {
        // 文件下所有的文件夹与文件列表
        List<File> fileList = folderRepository.findFiles(id);
        List<Folder> folderList = folderRepository.findSubFolders(id);

        // 返回给前端的数据不区分文件和文件夹，这里合并到一个集合中去
        ArrayList<FolderDetail.File> fileVoList = new ArrayList<>();
        for (Folder folder : folderList) {
            FolderDetail.File file = FolderDetail.File.builder()
                    .id(folder.getId())
                    .name(folder.getName())
                    .modifyTime(folder.getModifyTime())
                    .isFolder(true)
                    .size(0L)
                    .build();
            fileVoList.add(file);
        }

        for (File file : fileList) {
            FolderDetail.File file0 = FolderDetail.File.builder()
                    .id(file.getId())
                    .name(file.getName())
                    .size(file.getSize())
                    .modifyTime(file.getCreateTime())
                    .isFolder(false)
                    .build();
            fileVoList.add(file0);
        }
        // 文件路径
        List<FolderDetail.FolderPathItem> pathList = getPathList(id);

        FolderDetail folderDetail = FolderDetail.builder()
                .path(pathList)
                .name(pathList.get(pathList.size() - 1).getName())
                .fileList(fileVoList)
                .build();
        return folderDetail;
    }


    /**
     * 生成树形结构
     * TODO 这里使用递归，面对大数据量可能会产生性能问题
     *
     * @param folderTreeVoList 树形结构的 list 形式 [id, parentId]
     *                         <==        Row: 1, ab, 0
     *                         <==        Row: 77, a, 1
     *                         <==        Row: 78, b, 1
     *                         <==        Row: 79, a - a, 77
     *                         <==        Row: 80, a - b, 77
     *                         <==        Row: 81, a - a - a, 79
     * @param currentNode
     * @return Node结点形式
     */
    private List<SubFolder> recur(List<SubFolder> folderTreeVoList, FolderTreeNode currentNode) {

        if (folderTreeVoList.size() == 0) {
            return folderTreeVoList;
        }

        Long currentId = currentNode.id;

        folderTreeVoList = folderTreeVoList.stream()
                .map(folder -> {
                    if (folder.getParentId().equals(currentId)) {
                        currentNode.children.add(new FolderTreeNode(folder.getId(), folder.getName()));
                    }
                    return folder;
                })
                .filter(folder -> !folder.getParentId().equals(currentId)) // 排除掉已经加入到子节点的元素
                .collect(Collectors.toList());


        for (FolderTreeNode folderChild : currentNode.children) {
            folderTreeVoList = recur(folderTreeVoList, folderChild);
        }

        return folderTreeVoList;
    }

}
