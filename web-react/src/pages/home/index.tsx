import {useParams} from "react-router";
import {useEffect, useState} from "react";

import {Header} from "./header.tsx";
import {FilePath} from "./file-path.tsx";
import {getSubFoldersAndFiles} from "@/api/file";
import {FolderDetail} from "@/api/file/type.ts";
import {FileList} from "./file-list.tsx";

import {useUserStore} from "@/store/user.ts";
import Pubsub from "pubsub-js";

export function Home() {
    const {id} = useParams();
    const [folderDetail, setFolderDetail] = useState<FolderDetail>(null);
    const userStore = useUserStore();

    // 刷新页面，完成新建文件夹等操作后要刷新页面
    const refresh = () => {
        getFiles();
    }
    Pubsub.subscribe("refresh", refresh);

    const getFiles = async () => {
        try {
            const response = await getSubFoldersAndFiles(id)
            setFolderDetail(response.data)
        } catch (error) {
            console.log(error)
        }
    }

    useEffect(() => {
        getFiles();
        userStore.folderId = id;
    }, [id])

    return (
        <div>
            <Header/>
            {folderDetail && <FilePath path={folderDetail.path}/>}
            {folderDetail && <FileList fileList={folderDetail.fileList}/>}
        </div>
    )
}