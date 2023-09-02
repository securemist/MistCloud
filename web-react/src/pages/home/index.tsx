import {useParams} from "react-router";
import React, {useEffect, useState} from "react";

import {Header} from "./header.tsx";
import {FilePath} from "./file-path.tsx";
import {getSubFoldersAndFiles} from "@/api/file";
import {FolderDetail} from "@/api/file/type.ts";
import {FileList} from "./file-list.tsx";

import {useUserStore} from "@/store/user.ts";
import Pubsub from "pubsub-js";

export function Home() {
    const getFiles = async () => {
        try {
            const response = await getSubFoldersAndFiles(id)
            setFolderDetail(response.data)
        } catch (error) {
            console.log(error)
        }
    }

    // 从路径中获取文件夹id
    const {id} = useParams();
    const userStore = useUserStore();
    userStore.folderId = id;
    const [folderDetail, setFolderDetail] = useState<FolderDetail>(null);

    useEffect(() => {
        getFiles();
    }, [id])

    const refresh = () => {
        getFiles();
    }
    // 刷新页面，完成新建文件夹等操作后要刷新页面
    Pubsub.subscribe("refresh", refresh);
    return (
        <div>
            <Header/>
            {folderDetail && <FilePath path={folderDetail.path}/>}
            {folderDetail && <FileList fileList={folderDetail.fileList}/>}

        </div>
    )
}