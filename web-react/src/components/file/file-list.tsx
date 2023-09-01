import styles from "./file-list.module.scss";
import React from "react";
import {File, FolderDetail} from "@/api/file/type.ts";
import {ReactComponent as FolderSvg} from "@/icons/folder.svg";
import {useNavigate} from "react-router-dom";

interface Props {
    fileList: File[]
}

export const FileList: React.FC<Props> = (props) => {
    const {fileList} = props;
    return (
        <div className={styles.container}>
            {
                fileList.map(file => {
                    return (
                        <FileItem key={file.id} file={file}/>
                    )
                })
            }
        </div>
    )
}

interface FileProps {
    file: File;
}

const FileItem: React.FC<FileProps> = (props) => {
    const {file} = props;
    const navigate = useNavigate();

    // 点击文件夹logo，进入文件夹
    const enterFolder = () => {
        navigate(`/home/${file.id}`)
    }

    return (
        <div className={styles["file-box"]}>
            {/*TODO 根据文件名计算出svg*/}
            <FolderSvg className={styles["file-img"]} onClick={enterFolder}/>
            <div className={styles["file-name"]}>{file.name}</div>
        </div>
    )
}