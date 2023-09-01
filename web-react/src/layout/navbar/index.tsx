import styles from "./navbar.module.scss"
import {Menu} from "./menu.tsx";

import {Dropdown, Space} from 'antd';
import SvgIcon from "@/components/SvgIcon/SvgIcon.tsx";

export function Navbar() {
    //头像框下拉菜单
    const items = [
        {
            key: "1",
            label: (
                <a rel="noopener noreferrer">
                    个人设置
                </a>
            )
        },
        {
            key: "2",
            label: (
                <a rel="noopener noreferrer">
                    退出登陆
                </a>
            )
        }
    ]
    return (
        <div className={styles.container}>
            <div className={styles.logo}>
                <SvgIcon name={"logo"} size={"40px"} className={styles["logo-icon"]}/>
                <div className={styles["logo-title"]}>
                    <span>Mist Cloud</span>
                </div>
            </div>

            <Menu/>

            {/*头像下拉框*/}
            <Dropdown menu={{items}} className={styles["drop-down"]}>
                <a onClick={(e) => e.preventDefault()}>
                    <Space>
                        <SvgIcon name={"avatar"} size={"40px"} className={styles["logo-link"]}/>
                    </Space>
                </a>
            </Dropdown>

            <SvgIcon name={"github"} size={"40px"} className={styles["logo-link"]}/>


        </div>
    )
}