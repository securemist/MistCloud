import styles from "./header.module.scss";
import {Button, Modal, Input, Dropdown, Space, message} from 'antd';
import React, {useRef, useState} from "react";
import {func} from "prop-types";
import {createFolder, searchFile} from "@/api/file";
import {removeToken} from "@/utils/auth.ts";
import {Simulate} from "react-dom/test-utils";
import input = Simulate.input;
import {useUserStore} from "@/store/user.ts";
import {useNavigate} from "react-router-dom";
import Pubsub from "pubsub-js";
export function Header() {
    return (
        <div className={styles["header-container"]}>
            <div className={styles["item"]}>
                <CreateFolderDialog/>
            </div>
            <div className={styles["item"]}>
                <UploadButton/>
            </div>
            <div className={styles["search-box"]}>
                <SearchBox/>
            </div>
        </div>
    )
}

/**
 * 新建文件夹button + dialog
 * @constructor
 */
const CreateFolderDialog: React.FC = () => {
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
        messageApi.warning("取消创建");
        setIsModalOpen(false);
    };
    return (
        <>
            {contextHolder}
            <Button type="primary" onClick={showModal}>
                新建文件夹
            </Button>
            <Modal title="请输入文件夹名称" open={isModalOpen} onOk={handleOk} onCancel={handleCancel}>
                <Input size="large" placeholder={"large size"} ref={inputRef} defaultValue={"原文件"}/>
            </Modal>
        </>
    );
}

/**
 * 上传按钮
 * @constructor
 */
const UploadButton: React.FC = () => {

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
        <Dropdown menu={{items}} placement={"bottom"}>
            <a onClick={(e) => e.preventDefault()}>
                <Button type="primary"> 上传</Button>
            </a>
        </Dropdown>
    )

}

/**
 * 搜索框
 * @constructor
 */
const SearchBox: React.FC = () => {

    const {Search} = Input;
    const onSearch = (value: string) => {
        // 发送请求搜索
        // searchFile(value)
    }

    return (
        <Search placeholder="input search text" onSearch={onSearch} enterButton/>
    )
}