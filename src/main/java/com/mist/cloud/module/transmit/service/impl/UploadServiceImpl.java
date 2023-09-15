package com.mist.cloud.module.transmit.service.impl;

import com.mist.cloud.core.exception.file.FileUploadException;
import com.mist.cloud.module.transmit.service.IUploadService;
import com.mist.cloud.core.config.IdGenerator;
import com.mist.cloud.core.constant.Constants;
import com.mist.cloud.module.file.model.pojo.FolderBrief;
import com.mist.cloud.module.transmit.context.Task;
import com.mist.cloud.module.transmit.service.TransmitSupport;
import com.mist.cloud.infrastructure.entity.File;
import com.mist.cloud.core.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.mist.cloud.core.utils.FileUtils.merge;

/**
 * @Author: securemist
 * @Datetime: 2023/8/19 17:01
 * @Description:
 */
@Service
@Slf4j
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
                .relativePath(task.getRelativePath())
                .build();

        // 添加记录
        fileRepository.addFile(file);
    }

    @Override
    public void uploadSingleFile(Long folderId, MultipartFile file) {
        String fileName = checkFileName(file.getOriginalFilename(), folderId);
        // 所有的单文件上传全部上传到根路径
        File newFile = File.builder()
                .id(IdGenerator.fileId())
                .name(fileName)
                .size(file.getSize())
                .type(FileUtils.getFileType(file.getName()))
                .folderId(folderId)
                .relativePath("/")
                .originName(file.getOriginalFilename())
                .md5("")
                .build();

        fileRepository.addFile(newFile);
    }


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
    @Override
    public Map<String, Long> createSubFolders(Map<String, String> identifierMap, Long parentId) throws FileUploadException {
        // 文件路径去重
        Set<String> pathSet = collectPath(identifierMap);
        if (pathSet == null || pathSet.size() == 0) {
            return new HashMap<>();
        }

        // 构造树形结构
        Node tree = generateTree(pathSet);
        // 数据库中创建文件夹
        Map<String, Long> idMap = createFolders(tree, parentId);
        return idMap;
    }

    @Override
    public void mergeFiles(HashMap<String, String> identifierMap, Map<String, Long> idMap) throws FileUploadException {
        for (String identifier : identifierMap.keySet()) {
            try {
                Task task = uploadTaskContext.getTask(identifier);

                /**
                 * 在创建文件夹的过程中已经创建了文件夹，生成了新的文件夹 id，需要替换掉 task 中原本的 folderId
                 * 上传文件不需要考虑这种情况
                 * task.setRelativePath("/" + fileInfo.getRelativePath());
                 *
                 * idMap 中的路径是不带有文件名的，relativePath带有文件名，获取生成的文件夹 id 需要适当调整
                 * 全部文件/java   |  全部文件/java/java.md
                 * relativePath.substring(0, relativePath.lastIndexOf('/'))
                 */
                if (idMap.size() != 0) {
                    String relativePath = task.getRelativePath();
                    if (relativePath.substring(1, relativePath.length()).contains("/")) {
                        task.setFolderId(idMap.get(relativePath.substring(0, relativePath.lastIndexOf('/'))));
                    }
                }

                String fileName = task.getFileName();
                String fileRealPath = fileConfig.getBasePath() + task.getRelativePath();
                String targetFolderPath = fileConfig.getUploadPath() + task.getFolderPath();


                // 合并文件并且算出 md5 值
                String newMD5 = "";
                try {
                    merge(fileRealPath, targetFolderPath, fileName);
                } catch (IOException e) {
                    throw new FileUploadException("file merge error in IO", new ArrayList<>(identifierMap.keySet()), e);
                }

                if (fileConfig.checkmd5) {
                    String md5 = identifierMap.get(identifier);
                    boolean ok = FileUtils.checkmd5(fileRealPath, md5);
                    if (!ok) {
                        throw new FileUploadException("file merge error because md5 is not equal", new ArrayList<>(identifierMap.keySet()));
                    }
                    task.setMD5(md5);
                }
                // 校验 md5 值
                uploadTaskContext.completeTask(identifier);
                // 数据库添加记录
                uploadFile(task);
                log.info("文件上传成功, 文件位置: {}", fileRealPath);
            } catch (FileUploadException e) {
                throw new FileUploadException(e.getMsg(), new ArrayList<>(identifierMap.keySet()));
            } catch (Exception e) {
                e.printStackTrace();
                throw new FileUploadException("file upload failed in controller", new ArrayList<>(identifierMap.keySet()));
            }

        }
    }

    private Set<String> collectPath(Map<String, String> identifierMap) throws FileUploadException {
        Set<String> pathSet = new HashSet<>();
        for (String identifier : identifierMap.keySet()) {
            Task task = uploadTaskContext.getTask(identifier);
            String relativePath = task.getRelativePath();

            // 排除掉单文件上传的情况
            // 这里的 relativePath 总是会带有 / 的，即使是单文件上传，也会是 /filename，需要排除这种情况
            if (relativePath.substring(1, relativePath.length()).contains("/")) {
                pathSet.add(relativePath);
            }
        }
        return pathSet;
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
            if (file0.getName().equals(fileName)) {
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

