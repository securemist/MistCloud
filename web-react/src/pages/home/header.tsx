import styles from "./header.module.scss";
import {Button, Modal, Input, Dropdown, Space, message} from 'antd';
import React, {forwardRef, useRef, useState} from "react";
import {createFolder, searchFile} from "@/api/file";
import {useUserStore} from "@/store/user.ts";
import {useNavigate} from "react-router-dom";
import Pubsub from "pubsub-js";

/**
 * 文件区顶部菜单
 * @constructor
 */
export function Header() {
    const refresh = () => {
         Pubsub.publish("refresh");
    }

    return (
        <div className={styles["header-container"]}>
            <CreateFolderDialog className={styles["item"]}/>
            <UploadButton className={styles["item"]}/>
            <Button className={styles["item"]} onClick={refresh}>刷新</Button>
            <SearchBox className={styles["search-box"]}/>
        </div>
    )
}

/**
 * 新建文件夹button + dialog
 * @constructor
 */
const CreateFolderDialog = forwardRef<HTMLDivElement, React.HTMLProps<HTMLDivElement>>((props, ref) => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [messageApi, contextHolder] = message.useMessage();
    const inputRef = useRef<any>(null);
    const userStore = useUserStore();
    const navigate = useNavigate();
    const showModal = () => {
        setIsModalOpen(true);
    };

    const handleOk = async () => {
        // 发送请求创建文件
        // 拿到输入值
        const value = inputRef.current.input.value
        // 校验名字
        // validFolderName(value)

        // 发送请求
        // 创建位置所在文件夹的id
        const parentId = userStore.folderId;
        try {
            await createFolder(parentId, value);
            messageApi.success("创建成功");
            Pubsub.publish("refresh");
        } catch (error) {
            console.log(error);
            messageApi.warning("创建失败")
        }
        setIsModalOpen(false);
    };

    const handleCancel = () => {
        setIsModalOpen(false);
    };
    return (
        <div ref={ref} {...props}>
            {contextHolder}
            <Button onClick={showModal}>
                新建文件夹
            </Button>
            <Modal title="请输入文件夹名称" open={isModalOpen} onOk={handleOk} onCancel={handleCancel}>
                <Input size="large" placeholder={"large size"} ref={inputRef} defaultValue={"原文件"}/>
            </Modal>
        </div>
    );
})

/**
 * 上传按钮
 * 我自己封装的组件是没有原生className等原生React组件的属性的，如何加上去
 * @constructor
 */
const UploadButton = forwardRef<HTMLDivElement, React.HTMLProps<HTMLDivElement>>((props, ref) => {
    const items = [
        {
            label: (
                <a rel="noopener noreferrer">上传文件</a>
            ),
            key: '0',
        },
        {
            label: (
                <a rel="noopener noreferrer">上传文件夹</a>
            ),
            key: '1',
        }
    ]
    return (
        <div ref={ref} {...props}>
            <Dropdown menu={{items}} placement={"bottom"}>
                <a onClick={(e) => e.preventDefault()}>
                    <Button> 上传</Button>
                </a>
            </Dropdown>
        </div>

    )
});

/**
 * 搜索框
 * @constructor
 */

const SearchBox =
    forwardRef<HTMLDivElement, React.HTMLProps<HTMLDivElement>>((props, ref) => {
        const {Search} = Input;
        const onSearch = (value: string) => {
            // 发送请求搜索
            // searchFile(value)
        }

        return (
            <div ref={ref} {...props}>
                <Search placeholder="搜索文件" onSearch={onSearch}/>
            </div>
        )
    });

