package com.mist.cloud.module.transmit.context.uploader;

import com.mist.cloud.core.config.IdGenerator;
import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.core.utils.Session;
import com.mist.cloud.module.file.model.pojo.FolderBrief;
import com.mist.cloud.module.transmit.context.Task;
import com.mist.cloud.module.transmit.context.support.UploaderSupport;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author: securemist
 * @Datetime: 2023/9/16 22:25
 * @Description: [又臭又长] 递归创建文件夹 !!已经废弃!!
 * <p>
 * 文件夹中的所有文件均采用分片上传的形式
 */
@Component
public class CompleteChunkFolderUploader extends UploaderSupport {
    /**
     * 给文件夹内所有的子子文件夹创建记录，返回路径与文件夹id的映射
     * <p>
     * 0 = "/a/c/folder.png"
     * 1 = "/a/b/zfile-4.1.5.war"
     * ===>
     * "/a/b" -> {Long@11242} 1702649967044333571
     * "/a" -> {Long@11240} 1702649967044333569
     * "/a/c" -> {Long@11238} 1702649967044333570
     */
    public Map<String, Long> createFolders(Map<String, String> identifierMap, Long parentId) throws FileUploadException {
        // 文件路径去重
        Set<String> pathSet = collectPath(identifierMap);
        if (pathSet == null || pathSet.size() == 0) {
            return new HashMap<>();
        }


        // 创建必要的路径
        Map<String, Long> idMap = createPath(parentId, pathSet);

        // 把新创建的folderId更新到task中去
        for (String identifier : identifierMap.keySet()) {
            Task task = taskExecutor.getTask(identifier);
            String relativePath = task.getRelativePath();
            if (relativePath.substring(1, relativePath.length())
                    .contains("/")) {
                task.setFolderId(idMap.get(relativePath.substring(0, relativePath.lastIndexOf('/'))));
            }
        }

        return idMap;
    }

    protected Map<String, Long> createPath(Long parentId, Set<String> pathSet) {
        // 构造树形结构
        Node tree = generateTree(pathSet);
        // 数据库中创建文件夹
        Map<String, Long> idMap = createFolders(tree, parentId);
        return idMap;
    }

    private Map<String, Long> createFolders(Node root, Long parentId) {
        // 拿到每个路径 path 对应的文件夹id
        Map<String, Long> idMap = new HashMap<>();
        idMap = treeToIdMap(root, idMap, "");

        List<FolderBrief> folderList = new ArrayList<>();
        folderList = treeToFolderList(root, folderList, parentId, 0);

        folderRepository.createFolders(folderList);
        return idMap;
    }

    private Node generateTree(Set<String> pathSet) {
        List<List<String>> list = new ArrayList<>();
        HashSet<List<String>> cache = new HashSet<>();

        int maxLen = 0;
        for (String path : pathSet) {
            List<String> list0 = Arrays.asList(path.substring(1, path.lastIndexOf('/'))
                    .split("/"));
            if (cache.contains(list0)) {
                continue;
            }
            maxLen = Math.max(maxLen, list0.size());
            list.add(list0);
            cache.add(list0);
        }

        String[][] pathList = new String[list.size()][maxLen];
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i)
                    .size(); j++) {
                String name = list.get(i)
                        .get(j);
                if (name == null) {
                    name = "";
                }
                pathList[i][j] = name;
            }
        }

        // 构造树性结构
        Node root = new Node("root");
        recur(root, pathList, 0, "");
        return root;
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
                    .userId(Session.getLoginId())
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
        if (!path.equals("")) { // 手动创建的 root 根目录为 "" ，需要排除
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

}
