import {useParams} from "react-router";
import axios from "axios";
import {useEffect, useState} from "react";
import {Header} from "@/components/file/header.tsx";
import {FilePath} from "@/components/file/file-path.tsx";
import {getSubFoldersAndFiles} from "@/api/file";
import {FolderDetail} from "@/api/file/type.ts";
import {FileList} from "@/components/file/file-list.tsx";

export function FilePage() {
    const {id} = useParams();
    const [folderDetail, setFolderDetail] = useState<FolderDetail>(null);

    const getFiles = async () => {
        try {
            const response = await getSubFoldersAndFiles(id)
            setFolderDetail(response.data)
        } catch (error) {
            console.log(error)
        }
    }

    useEffect(() => {
        getFiles()
    }, [id])

    return (
        <div>
            <Header/>
            {folderDetail && <FilePath path={folderDetail.path}/>}
            {folderDetail && <FileList fileList={folderDetail.fileList}/>}
        </div>
    )
}