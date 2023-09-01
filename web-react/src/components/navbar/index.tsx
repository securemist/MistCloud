import styles from "./navbar.module.scss"
import {ReactComponent as Logo} from "@/icons/logo.svg";
import {Menu} from "@/components/navbar/menu.tsx";

import {ReactComponent as Avatar} from "@/icons/avatar.svg";
import {ReactComponent as GithubLink} from "@/icons/github.svg";

import {Dropdown, Space} from 'antd';

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
                <Logo className={styles["logo-icon"]}/>
                <div className={styles["logo-title"]}>
                    <span>Mist Cloud</span>
                </div>
            </div>

            <Menu/>
            {/*头像下拉框*/}
            <Dropdown menu={{items}} className={styles["drop-down"]}>
                <a onClick={(e) => e.preventDefault()}>
                    <Space>
                        <Avatar
                            className={styles["logo-link"]}
                        />
                    </Space>
                </a>
            </Dropdown>

            <GithubLink
                className={styles["logo-link"]}
                onClick={() => {
                    window.open("https://github.com")
                }}
            />



        </div>
    )
}