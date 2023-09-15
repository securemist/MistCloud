package com.mist.cloud.module.file.service;

import com.mist.cloud.infrastructure.entity.Folder;
import com.mist.cloud.module.file.model.pojo.FolderDetail;
import com.mist.cloud.module.file.repository.IFileRepository;
import com.mist.cloud.module.file.repository.IFolderRepository;
import com.mist.cloud.module.user.repository.IUserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static cn.dev33.satoken.stp.StpUtil.getLoginId;

/**
 * @Author: securemist
 * @Datetime: 2023/8/25 19:45
 * @Description:
 */
@Service
public abstract class FileServiceSupport implements IFileStrategy {
    @Resource
    public IFolderRepository folderRepository;
    @Resource
    public IFileRepository fileRepository;
    @Resource
    public IUserRepository userRepository;

    /**
     * 获取文件夹的路径List形式  [{id, name}] /[{id, name}]
     *
     * @param id
     * @return
     */
    public List<FolderDetail.FolderPathItem> getPathList(Long id) {
        // 获取文件夹所处路径，从数据库查询出来的路径集合不具有顺序性，这里需要调整顺序返回给前端
        List<Folder> list = folderRepository.getFolderPath(id);
        List<FolderDetail.FolderPathItem> pathList = new ArrayList<>();

        Long userId = Long.parseLong(getLoginId().toString());
        Long rootFolderId = userRepository.getRootFolderId(userId);

        int cnt = 0;
        Long currentParentId = rootFolderId;
        while (cnt < list.size()) {
            for (Folder folder : list) {
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
