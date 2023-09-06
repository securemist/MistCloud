import React from "react";
import styles from "./upload-list.module.scss"

export const UploadList = (props: React.HTMLAttributes<any>) => {
    return (
       <div>
           <UploadListButton/>
       </div>
    )
}

const UploadListButton = (props: React.HTMLAttributes<any>) => {
        return (
        <div className={styles["upload-list-button"]}>
            <span className="menu-active">上传列表</span>
        </div>
    )
}