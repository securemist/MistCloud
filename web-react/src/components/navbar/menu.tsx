import styles from "./menu.module.scss"
import {useNavigate} from "react-router-dom";

export function Menu() {
    const navigate = useNavigate();

    return (
        <div className={styles["menu-container"]}>
            <span className={styles["menu-item"]} onClick={
                () => {
                    navigate("/home/1")
                }
            }>我的文件</span>
            <span className={styles["menu-item"]} onClick={
                () => {
                    navigate("/recycle")
                }
            }>回收站</span>
            <span className={styles["menu-item"]}>我的分享</span>
        </div>
    )
}