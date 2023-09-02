import styles from "./file-list.module.scss";
import React, {MutableRefObject, useEffect, useRef, useState} from "react";
import {File} from "@/api/file/type.ts";
import {useNavigate} from "react-router-dom";
import {Dropdown, MenuProps, Popover} from "antd";
import SvgIcon from "@/components/SvgIcon/SvgIcon.tsx";
import {useUserStore} from "@/store/user.ts";
import {timestampToTime} from "@/utils/common.ts";
import boolean from "async-validator/dist-types/validator/boolean";

/**
 * 文件列表
 * @constructor
 */
export const FileList: React.FC<{ fileList: File[] }> = (props) => {
    const {fileList} = props;
    const userStore = useUserStore();
    const navigate = useNavigate();
    // 屏蔽鼠标右键点击事件
    document.oncontextmenu = function (e) {
        return false;
    }

    // 双击进入文件夹
    const enterFolder = (id: string) => {
        navigate(`/home/${id}`);
        userStore.clearFiles();
    }

    // 单击选中文件
    const selectFile = (id: string) => {
        const selectedFiles = userStore.selectedFiles;
        // 如果已经选中就取消选中
        if (selectedFiles.has(id)) {
            const ids: string[] = [id]
            userStore.removeFile(ids)
            return
        }
        const ids: string[] = [id]
        userStore.addFile(ids)
    }

    /**
     * 处理鼠标事件，单击进入选中文件，双击进入文件夹
     */
    let clicked: boolean = false;
    let timeOutId: NodeJS.Timeout | null = null;
    const clickFile = (id: string) => {
        if (clicked) { // 双击
            enterFolder(id);
            clicked = false
            clearTimeout(timeOutId);
        } else {  // 单击
            clicked = true;
            timeOutId = setTimeout(() => {
                clearTimeout(timeOutId);
                clicked = false;
                selectFile(id);
            }, 200); // 设置延迟时间，单位为毫秒
        }
    };

    // 切换文件列表的视图模式
    const view = userStore.iconView;
    const [iconView, setIconView] = useState(view);
    useEffect(() => {
            setIconView(view);
        },
        [view])

    //监听已经选中的文件set集合
    const selectedFiles = useUserStore.getState().selectedFiles;
    const [set, setSelectFilesSet] = useState(selectedFiles);
    useEffect(() => {
            setSelectFilesSet(selectedFiles)
        },
        [selectedFiles.size])

    return (
        <div className={styles.container}>
            {
                // 列表视图下的顶栏
                iconView ? <></>
                    :
                    <div className={styles.navbar}>
                        <input type={"checkbox"} className={styles["navbar-checkbox"]} defaultChecked={false}/>
                        <span className={styles["navbar-name"]}>文件名</span>
                        <span className={styles["navbar-options"]}>类型</span>
                        <span className={styles["navbar-time"]}>修改时间</span>
                    </div>
            }
            {
                // 文件列表
                fileList.map(file => {
                    return (
                        <div>
                            {
                                iconView ?
                                    <div className={styles["icon-view"]}>
                                        <FileIconView file={file} key={file.id} isSelected={set.has(file.id)}
                                                      clickFile={clickFile}
                                                      selectFile={selectFile}
                                                      enterFolder={enterFolder}/>
                                    </div>
                                    :
                                    <div className={styles["list-view"]}>
                                        <FileListView file={file} key={file.id} isSelected={set.has(file.id)}
                                                      clickFile={clickFile}
                                                      selectFile={selectFile}
                                                      enterFolder={enterFolder}/>
                                    </div>
                            }
                        </div>
                    )
                })
            }
        </div>
    )
}

// props如何接受一个函数
interface FileListProps {
    file: File,
    isSelected: boolean,
    clickFile: (id: string) => void,
    selectFile: (id: string) => void,
    enterFolder: (id: string) => void,
}

/**
 * 单个文件item 图标形式
 * @param props
 */
const FileListView: React.FC<FileListProps> = (props) => {
    const {file, isSelected, clickFile, selectFile} = props;
    const navigate = useNavigate();
    const userStore = useUserStore();

    const items: MenuProps['items'] = [
        {
            key: '2', label: <span onClick={() => {
                rename(file.id)
            }}
            >重命名</span>,
        },
        {
            key: '3', label: <span onClick={() => {
                copy(file.id)
            }}
            >复制</span>,
        },
        {
            key: '4', label: <span onClick={() => {
                move(file.id)
            }}
            >剪贴</span>,
        },
    ];

    return (
        <div className={styles["file-box"]}>
            <input type={"checkbox"} className={styles["checkbox"]}
                   defaultChecked={false}
                   checked={isSelected}
                   onChange={() => {
                       selectFile(file.id)
                   }}/>
            <SvgIcon name={"folder"} size={"65px"} className={styles["file-img"]}/>
            <div className={styles["file-name"]} onClick={() => clickFile(file.id)}>
                {file.name}
            </div>
            <div className={styles["modify-time"]}>
                {timestampToTime(file.modifyTime)}
            </div>
            <div className={styles["file-size"]}>{file.isFolder ? "文件夹" : file.size}</div>
            <div className={styles["options-container"]}>
                <SvgIcon name={"download"} size={"16px"} className={styles.icon}/>
                <SvgIcon name={"delete0"} size={"16px"} className={styles.icon}/>
                <Dropdown placement="bottom" menu={{items}} trigger={['hover']}>
                    <SvgIcon name={"drop-down"} size={"16px"} className={styles.icon}/>
                </Dropdown>
            </div>
        </div>
    )
}

const FileIconView: React.FC<FileListProps> = (props) => {
    const {file, clickFile, isSelected, selectFile} = props;
    const navigate = useNavigate();

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

    const content = (
        <div>
            <div>文件名: {file.name}</div>
            <div>文件大小: {file.isFolder ? "--------" : file.size}</div>
            <div>修改时间: {timestampToTime(file.modifyTime)}</div>
        </div>
    )

    return (
        <div className={styles["file-box"]}>
            <input type={"checkbox"} className={styles["checkbox"]}
                   defaultChecked={false}
                   style={isSelected ? {display: "block"} : {}}
                   checked={isSelected}
                   onChange={() => {
                       selectFile(file.id)
                   }}/>
            {/*下拉框实现鼠标右键菜单*/}
            <Dropdown menu={{items}} trigger={['contextMenu']}>
                <SvgIcon name={"folder"} size={"65px"} className={styles["file-img"]}
                         onClick={() => {
                             clickFile(file.id)
                         }}/>
            </Dropdown>
            <Popover placement={"bottom"} content={content} title="文件信息">
                <div className={styles["file-name"]}>{file.name}</div>
            </Popover>
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