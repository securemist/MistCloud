import styles from "./file-path.module.scss";
import React, {useEffect, useRef} from "react";
import {FolderDetail, PathItem} from "@/api/file/type.ts";
import {useNavigate} from "react-router-dom";
import {Breadcrumb, Button, Space} from "antd";
import SvgIcon from "@/components/SvgIcon/SvgIcon.tsx";
import {SearchOutlined} from "@ant-design/icons";
import {useUserStore} from "@/store/user.ts";
import Pubsub from "pubsub-js";

interface Props {
    path: PathItem[]
}

/**
 * 文件所在路径
 * @constructor
 */
export const FilePath: React.FC<Props> = (props) => {
    const {path} = props
    const navigate = useNavigate();
    const userStore = useUserStore();
    const goBack = () => {
        const lastId = path[path.length - 2].id
        navigate(`/home/${lastId}`)
    }
    const enterFolder = (id: string) => {
        return () => {
            navigate(`/home/${id}`)
        }
    }

    const items = []

    path.forEach(item => {
        items.push({
            id: item.id,
            title: (
                <span className={styles["item"]}
                      onClick={enterFolder(item.id)}
                >{item.name}</span>
            )
        })
    })


    const iconView = userStore.iconView;
    // 切换视图
    const changeView = () => {
        userStore.checkView();
    }

    return (
        <>
            <div className={styles["catalogue-container"]}>
                {path.length > 1 ?
                    (
                        <SvgIcon name={"go-back"} size={"20px"} className={styles["back-icon"]} onClick={goBack}/>
                    )
                    : <div className={styles["back-icon"]}></div>}

                <div className={styles["item-list"]}>
                    <Breadcrumb
                        separator={">"}
                        items={items}
                    />
                </div>

                <div className={styles["options"]}>
                    {/*<div className={styles["item"]}>*/}
                    {/*    <SvgIcon name={"icon"} size={"14px"} className={styles["svg"]}/>*/}
                    {/*    目录树*/}
                    {/*</div>*/}

                    {/*<div*/}
                    {/*    className={styles["item"]}>*/}
                    {/*    <SvgIcon name={"icon"} size={"14px"} className={styles["svg"]}/>*/}
                    {/*    全部*/}
                    {/*</div>*/}

                    <div onClick={changeView}
                         className={styles["item"]}
                    >
                        {
                            iconView ?
                                <div>
                                    <SvgIcon name={"list"} size={"14px"} className={styles["svg"]}/>
                                    <span>列表</span>
                                </div>
                                :
                                <div>
                                    <SvgIcon name={"icon"} size={"14px"} className={styles["svg"]}/>
                                    <span>图标</span>
                                </div>
                        }
                    </div>

                </div>

            </div>
            <div>

            </div>
        </>
    )
}
