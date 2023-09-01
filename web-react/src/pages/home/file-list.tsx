import styles from "./file-list.module.scss";
import React from "react";
import {File, FolderDetail} from "@/api/file/type.ts";
import {useNavigate} from "react-router-dom";
import {useUserStore} from "@/store/user.ts";
import {Dropdown, MenuProps} from "antd";
import SvgIcon from "@/components/SvgIcon/SvgIcon.tsx";

/**
 * 文件列表
 * @constructor
 */
export const FileList: React.FC<{ fileList: File[] }> = (props) => {
    const userStore = useUserStore();

    const {fileList} = props;
    return (
        <div className={styles.container}>
            {
                fileList.map(file => {
                    return (
                        <FileItem file={file} key={file.id}/>
                    )
                })
            }
        </div>
    )
}

/**
 * 单个文件item
 * @param props
 * @constructor
 */
const FileItem: React.FC<{ file: File }> = (props) => {
    const {file} = props;
    const navigate = useNavigate();

    // 点击文件夹logo，进入文件夹
    const enterFolder = () => {
        navigate(`/home/${file.id}`)
    }

    const items: MenuProps['items'] = [
        {
            key: '1', label: <a onClick={() => {
                download(file.id)
            }}>下载</a>,
        },
        {
            key: '2', label: <a onClick={() => {
                rename(file.id)
            }}>重命名</a>,
        },
        {
            key: '3', label: <a onClick={() => {
                copy(file.id)
            }}>复制</a>,
        },
        {
            key: '4', label: <a onClick={() => {
                move(file.id)
            }}>移动</a>,
        },
        {
            key: '5', label: <a onClick={() => {
                deleteFile(file.id)
            }}>删除</a>,
        },
    ];

    return (
        <div className={styles["file-box"]}>
            {/*下拉框实现鼠标右键菜单*/}
            <Dropdown menu={{items}} trigger={['contextMenu']}>
                <SvgIcon name={"folder"} size={"65px"} className={styles["file-img"]} onClick={enterFolder}/>
            </Dropdown>
            <div className={styles["file-name"]}>{file.name}</div>
        </div>
    )
}

// ============================================================================
// 文件CRUD操作
const download = (id: string) => {
    console.log("下载")

}

const rename = (id: string) => {
    console.log("rename")
}

const copy = (id: string) => {
    console.log("copy")

}

const move = (id: string) => {
}

const deleteFile = (id: string) => {
}