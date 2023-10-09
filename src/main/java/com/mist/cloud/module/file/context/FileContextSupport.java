package com.mist.cloud.module.file.context;

import com.mist.cloud.infrastructure.entity.Folder;
import com.mist.cloud.module.file.model.pojo.FolderDetail;
import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.module.user.repository.IUserRepository;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/17 09:40
 * @Description:
 */
public class FileContextSupport {
    @Resource
    public IFileRepository fileRepository;
    @Resource
    public IFolderRepository folderRepository;
    @Resource
    private IUserRepository userRepository;

    /**
     * 获取文件夹的路径List形式  [{id, name}] /[{id, name}]
     *
     * @param id
     * @return
     */
    public List<FolderDetail.FolderPathItem> getPathList(Long id) {
        Long folderId = id;
        if(!fileRepository.isFolder(id)) {
            folderId = fileRepository.findFile(id).getFolderId();
        }
        // 获取文件夹所处路径，从数据库查询出来的路径集合不具有顺序性，这里需要调整顺序返回给前端
        List<Folder> folderlist = folderRepository.getFolderPath(folderId);
        List<FolderDetail.FolderPathItem> pathList = new ArrayList<>();

        // 找到根目录
        Long currentParentId = null;
        for (Folder folder : folderlist) {
            if (folder.getParentId().equals(0L)) {
                currentParentId = folder.getId();
                folderlist.remove(folder);
                pathList.add(new FolderDetail.FolderPathItem(folder.getId(), folder.getName()));
                break;
            }
        }


        int cnt = 0;
        while (cnt < folderlist.size()) {
            for (Folder folder : folderlist) {
                if (folder.getParentId().equals(currentParentId)) {
                    pathList.add(new FolderDetail.FolderPathItem(folder.getId(), folder.getName()));
                    currentParentId = folder.getId();
                    cnt++;
                    break;
                }
            }
        }
        return pathList;
    }
}
