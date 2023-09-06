package com.mist.cloud.aggregate.tansmit.service.impl;

import com.mist.cloud.aggregate.tansmit.service.IUploadService;
import com.mist.cloud.common.config.IdGenerator;
import com.mist.cloud.common.constant.Constants;
import com.mist.cloud.aggregate.file.model.entity.FolderBrief;
import com.mist.cloud.aggregate.tansmit.context.Task;
import com.mist.cloud.aggregate.tansmit.service.TransmitSupport;
import com.mist.cloud.infrastructure.DO.File;
import com.mist.cloud.common.utils.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author: securemist
 * @Datetime: 2023/8/19 17:01
 * @Description:
 */
@Service
public class UploadServiceImpl extends TransmitSupport implements IUploadService {
    @Override
    @Transactional
    public void uploadFile(Task task) {
        // 校验文件名。防止重名
        String fileName = checkFileName(task.getFileName(), task.getFolderId());

        // 添加记录
        File file = File.builder()
                .id(IdGenerator.fileId())
                .name(fileName)
                .size(task.getFileSize())
                .type(FileUtils.getFileType(fileName))
                .folderId(task.getFolderId())
                .originName(task.getFileName())
                .md5(task.getMD5())
                .build();

        // 添加记录
        fileRepository.addFile(file);
    }

    /**
     * "/全部文件/视频/满江红" => ["全部文件", "视频", "满江红"]
     *
     * @param parentId
     * @param pathSet
     * @return
     */
    @Override
    public Map<String, Long> uploadFolder(Long parentId, Set<String> pathSet) {
        List<List<String>> list = new ArrayList<>();
        HashSet<List<String>> cache = new HashSet<>();
        if(pathSet == null || pathSet.size() == 0){
            return new HashMap<>();
        }

        int maxLen = 0;
        for (String path : pathSet) {
            List<String> list0 = Arrays.asList(path.substring(1, path.lastIndexOf('/')).split("/"));

            if (cache.contains(list0)) {
                continue;
            }
            maxLen = Math.max(maxLen, list0.size());
            list.add(list0);
            cache.add(list0);
        }

        String[][] pathList = new String[list.size()][maxLen];
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                String name = list.get(i).get(j);
                if (name == null) {
                    name = "";
                }
                pathList[i][j] = name;
            }
        }

        // 构造树性结构
        Node root = new Node("root");
        recur(root, pathList, 0, "");

        // 拿到每个路径 path 对应的文件夹id
        Map<String, Long> idMap = new HashMap<>();
        idMap = treeToIdMap(root, idMap, "");

        List<FolderBrief> folderList = new ArrayList<>();
        folderList = treeToFolderList(root, folderList, parentId, 0);

        folderRepository.createFolders(folderList);
        return idMap;
    }

    /**
     * 树形结构转换为 folder list 集合，方便插入数据库
     * <p>
     * 为什么要排除第一层的结点，因为在构建树形结构的同时额外构造了一个不存在的根节点，这里需要排除掉
     *
     * @param root
     * @param folderList
     * @param parentId
     * @return
     */
    private List<FolderBrief> treeToFolderList(Node root, List<FolderBrief> folderList, Long parentId, int depth) {
        if (depth != 0) {
            FolderBrief folder = FolderBrief.builder()
                    .id(root.id)
                    .name(root.name)
                    .userId(Constants.DEFAULT_USERID)
                    .parentId(parentId)
                    .build();
            folderList.add(folder);
        }

        if (root.child.size() == 0) {
            return folderList;
        }

        for (Node node : root.child) {
            folderList = treeToFolderList(node, folderList, depth == 0 ? parentId : root.id, depth + 1);
        }
        return folderList;
    }


    /**
     * 由树型结构拿到路径对应的文件夹 id
     * 与最开始的 pathSet 中的路径是一一对应的
     *
     * @param root
     * @param idMap
     * @param path
     * @return <p>
     * <p>
     * 文件列表
     * [全部文件/视频/满江红  => id]
     * [全部文件/照片  => id]]
     * [全部文件/文件  => id]]
     * [docker/mysql  => id]]
     * [Redis/null  => id]]
     */
    private Map<String, Long> treeToIdMap(Node root, Map<String, Long> idMap, String path) {
        if(!path.equals("")){ // 手动创建的 root 根目录为 "" ，需要排除
            idMap.put(path, root.id);
        }

        if (root.child.size() == 0) {
            return idMap;
        }

        for (Node node : root.child) {
            idMap = treeToIdMap(node, idMap, path + "/" + node.name);
        }
        return idMap;
    }

    /**
     * 构造树形结构，暴力搜索
     *
     * @param root
     * @param pathList   所有数据
     * @param depth      递归深度
     * @param parentName 上一层的文件夹名称，
     *                   如果这一层的前一个文件夹 pathList[i][depth - 1] == parentName，就说明这个文件夹是上一层文件夹的子文件夹
     *
     *                   <p>
     *                   文件列表
     *                   [全部文件  视频  满江红]
     *                   [全部文件   照片  null]
     *                   [全部文件   文件  null]
     *                   [docker   mysql  null]
     *                   [Redis    null  null]
     *
     *                   <p>
     *                   树形结构
     *                   |--全部文件
     *                   |    |--视频
     *                   |    |    |--满江红
     *                   |    |--照片
     *                   |    |--文件
     *                   |--docker
     *                   |    |--mysql
     *                   |--Redis
     **/
    private void recur(Node root, String[][] pathList, int depth, String parentName) {
        if (depth == pathList[0].length) {
            return;
        }

        int count = 0;
        HashMap<String, Node> map = new HashMap<>();
        for (int i = 0; i < pathList.length; i++) {
            String name = pathList[i][depth];
            if ((depth != 0 && !parentName.equals(pathList[i][depth - 1])) || name == null) {
                continue;
            }

            Node node = map.get(name);

            if (node == null) {
                node = new Node(name);
                map.put(name, node);
                root.child.add(node);
            }
        }

        for (Node node : root.child) {
            recur(node, pathList, depth + 1, node.name);
        }
    }

    class Node {
        public String name;
        public List<Node> child = new ArrayList<>();
        public Long id;

        public Node(String name) {
            this.name = name;
            id = IdGenerator.fileId();
        }
    }


    /**
     * 校验文件名
     * 如果当前文件夹下已经有一个同名的文件，将会赋予新的文件名，根据重名的次数添加后缀
     *
     * @param fileName 原有的文件名
     * @param folderId 所处文件夹 id
     * @return
     */
    public String checkFileName(String fileName, Long folderId) {
        // 查看是否同名
        List<File> files = folderRepository.findFiles(folderId);
        File file = null;
        for (File file0 : files) {
            if(file0.getName().equals(fileName)){
                file = file0;
            }
        }

        // 没有发生重名
        if (file == null) {
            return fileName;
        }

        // 新的后缀名 _1  _2 ......
        String suffix = String.valueOf(file.getDuplicateTimes() + 1);

        // 得到文件的新名字
        int index = fileName.lastIndexOf(".");
        String name; // 文件名
        String extentionName = ""; // 文件扩展名

        if (index == -1) { // 文件没有后缀
            name = fileName;
        } else {
            name = fileName.substring(0, index);
            extentionName = fileName.substring(index, fileName.length());
        }

        // 添加后缀 _1 / _2 并合并文件名
        fileName = name + "_" + suffix + extentionName;

        // 更新重名次数
        fileRepository.updateFileDuplicateTimes(file.getId());

        return fileName;
    }

    private String getFileNewName(String originalName, String suffix) {
        // 判断文件名有没有后缀
        int index = originalName.lastIndexOf(".");
        String name; // 文件名
        String suffixName = ""; // 后缀

        if (index == -1) { // 文件没有后缀
            name = originalName;
        } else {
            name = originalName.substring(0, index);
            suffixName = originalName.substring(index, originalName.length());
        }

        // 添加后缀 _1 / _2 并合并文件名
        return name + "_" + suffix + suffixName;
    }
}

