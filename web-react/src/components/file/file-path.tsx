import styles from "./file-path.module.scss";
import React, {useRef} from "react";
import {ReactComponent as BackSvg} from "@/icons/go-back.svg";
import {FolderDetail, PathItem} from "@/api/file/type.ts";
import {useNavigate} from "react-router-dom";

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
    const goBack = () => {
        const lastId = path[path.length - 2].id
        navigate(`/home/${lastId}`)
    }
    const enterFolder = (id: string) => {
        return () => {
            navigate(`/home/${id}`)
        }
    }

    return (
        <>
            <div className={styles["catalogue-container"]}>
                {path.length > 1 ?
                    <BackSvg className={styles["back-icon"]} onClick={goBack}/>
                    : <div className={styles["back-icon"]}></div>}

                <div className={styles["item-list"]}>
                    {
                        path.map(item => {
                            return (
                                <span key={item.id}
                                      className={styles.item}
                                      onClick={enterFolder(item.id)}
                                >{item.name}</span>
                            )
                        })
                    }
                </div>
            </div>
        </>
    )
}

