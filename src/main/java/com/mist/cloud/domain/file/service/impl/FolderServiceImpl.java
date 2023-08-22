package com.mist.cloud.domain.file.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.mist.cloud.common.constant.Constants;
import com.mist.cloud.common.config.FileConfig;
import com.mist.cloud.common.config.IdGenerator;
import com.mist.cloud.domain.file.model.dto.FolderDto;
import com.mist.cloud.domain.file.model.dto.UserCapacityDto;
import com.mist.cloud.domain.file.model.entity.FolderCreateReq;
import com.mist.cloud.domain.file.model.tree.FolderTreeNode;
import com.mist.cloud.domain.file.model.tree.SubFolder;
import com.mist.cloud.domain.file.repository.IFileRepository;
import com.mist.cloud.domain.file.repository.IFolderRepository;
import com.mist.cloud.domain.file.service.IFileService;
import com.mist.cloud.domain.file.service.IFolderService;
import com.mist.cloud.domain.tansmit.service.impl.UploadServiceImpl;
import com.mist.cloud.infrastructure.dao.FileMapper;
import com.mist.cloud.infrastructure.dao.FolderMapper;
import com.mist.cloud.infrastructure.dao.UserMapper;
import com.mist.cloud.common.exception.file.FolderException;
import com.mist.cloud.common.exception.RequestParmException;
import com.mist.cloud.infrastructure.DO.File;
import com.mist.cloud.infrastructure.DO.Folder;
import com.mist.cloud.domain.file.model.entity.FileSelectReq;
import com.mist.cloud.domain.file.model.entity.FolderDetail;
import com.mist.cloud.domain.file.model.entity.FolderSelectReq;
import com.mist.cloud.common.utils.DateTimeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author: securemist
 * @Datetime: 2023/7/19 18:47
 * @Description:
 */
@Service
public class FolderServiceImpl implements IFolderService {
    @Resource
    private FileConfig fileConfig;
    @Resource
    private IFileRepository fileRepository;
    @Resource
    private IFolderRepository folderRepository;

    @Override
    public FolderDto createFolder(Long parentId, String folderName) throws FolderException {
        // 校验传入的 parentId 是否存在
        // TODO

        Folder folder = folderRepository.findFolder(parentId);
        if (folder == null) {
            throw new RequestParmException();
        }

        // 查看当前目录下同名文件夹是否存在
        List<Folder> subFolders = folderRepository.findSubFolders(parentId);
        for (Folder subFolder : subFolders) {
            if (subFolder.getName().equals(folderName)) { // 出现重名
                throw new FolderException(Constants.Response.FOLDER_DUPLICATE_NAME.getMsg());
            }
        }

        // 创建文件夹,返回创建之后的主键 ID
        Long id = folderRepository.createFolder(folderName, parentId);

        FolderDto folderDto = FolderDto.builder()
                .userId(Constants.DEFAULT_USERID)
                .id(id)
                .name(folderName)
                .parentId(parentId)
                .createTime(DateTimeUtils.now())
                .size(0L).build();

        return folderDto;
    }

    @Override
    public UserCapacityDto getCapacityInfo() {
        // TODO
        // 获取用户已经使用了的云盘容量 统计根目录的大小即可
//        Long usedCapacity = folderRepository.getRootDirFolderSize(Constants.DEFAULT_USERID);
        // 获取用户云盘总容量
//        Long totalCapacity = userMapper.getTotalCapacity(Constants.DEFAULT_USERID);

//        UserCapacityDto userCapacityDto = UserCapacityDto
//                .builder()
//                .userId(Constants.DEFAULT_USERID)
//                .totalCapacity(totalCapacity)
//                .usedCapacity(usedCapacity)
//                .build();

//        return userCapacityDto;
        return null;
    }

    @Override
    public void rename(Long folderId, String folderName) {
        FolderSelectReq folderSelectReq = FolderSelectReq.builder()
                .folderName(folderName)
                .id(folderId)
                .userId(Constants.DEFAULT_USERID).build();

        folderRepository.renameFolder(folderId, folderName);
    }

    @Override
    public void deleteFolder(Long folderId, Boolean realDelete) {
        /**
         * 递归删除该文件夹下所有的子文件夹和文件
         * 这个 sql 语句很复杂，用到了自定函数，具体的放在 doc 目录下了
         * 这个 sql 放在 doc 目录下了
         */

        if (realDelete) {
            folderRepository.realDeleteFolderRecursive(folderId);
        } else {
            folderRepository.deleteFolderRecursive(folderId);
        }


    }

    @Override
    public FolderTreeNode getFolderTree() {
        Long rootDirId = 1L; // 根文件夹 id
        List<Folder> folderList = folderRepository.getFolderTree(rootDirId);

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
    public FolderDetail getFiles(Long folderId) {

        // 获取文件下的所有文件和文件夹信息
        FileSelectReq fileSelectReq = FileSelectReq
                .builder()
                .folderId(folderId)
                .build();

        FolderSelectReq folderSelectReq = FolderSelectReq.builder()
                .userId(Constants.DEFAULT_USERID)
                .parentId(folderId)
                .build();

        List<File> fileList = folderRepository.findFiles(folderId);
        List<Folder> folderList = folderRepository.findSubFolders(folderId);


        // 获取文件夹所处路径
        List<Folder> list = folderRepository.getFolderPath(folderId);
        List<FolderDetail.FolderPathItem> path = new ArrayList<>();

        int cnt = 0;
        Long rootDirId = 0L;
        Long currentParentId = rootDirId;
        while (cnt < list.size()) {
            for (Folder folder : list) {
                if (folder.getParentId().equals(currentParentId)) {
                    path.add(new FolderDetail.FolderPathItem(folder.getId().toString(), folder.getName()));
                    currentParentId = folder.getId();
                    cnt++;
                    break;
                }
            }
        }

        return new FolderDetail(path.get(path.size() - 1).getName(), path, folderList, fileList);
    }

    /**
     * @param folderId       复制的文件夹 id
     * @param targetFolderId 目标文件夹 id
     *                       要实现复制文件夹，需要对被复制的文件夹复制一条记录，同时对其所有的子文件夹和文件同样需要复制一条记录
     *                       但是因为 id 使用到是雪花 id，复制记录需要传入 id，数据库无法自己生成
     */
    @Override
    public void copyFolder(Long folderId, Long targetFolderId) throws FolderException {
        // 判断目标文件夹内是否已存在同名的文件夹
        List<Folder> subFolders = folderRepository.findSubFolders(targetFolderId);
        Folder folder = folderRepository.findFolder(folderId);

        if (folder != null) {
            for (Folder subFolder : subFolders) {
                if (subFolder.getName().equals(folder.getName())) {
                    throw new FolderException("文件夹已存在");
                }
            }
        }

        copyFolderRecur(folderId, targetFolderId);
    }


    // TODO 这里递归操作数据库，暂时没想到更好的方法
    private void copyFolderRecur(Long folderId, Long targetFolderId) {
        // 复制当前文件夹
        Long newFolderId = folderRepository.copyFolder(folderId, targetFolderId);

        // 复制当前文件夹的子文件
        List<Long> fileIds = folderRepository.findFilesId(folderId);
        for (Long fileId : fileIds) {
            fileRepository.copyFile(fileId, newFolderId);
        }

        // 递归复制所有子文件夹
        List<Long> childFolderIds = folderRepository.findSubFoldersId(folderId);
        for (Long childFolderId : childFolderIds) {
            copyFolderRecur(childFolderId, newFolderId);
        }
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
