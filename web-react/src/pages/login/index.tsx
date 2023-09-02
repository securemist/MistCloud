import styles from "./login.module.scss";
import React, {useEffect} from 'react';
import {Button, Checkbox, Form, Input, message} from 'antd';
import {validLoginForm} from "@/utils/valid.ts";
import {login} from "@/api/user";
import {useNavigate} from "react-router-dom";

type FieldType = {
    username?: string;
    password?: string;
};

export const Login: React.FC = () => {
    const navigate = useNavigate();
    const [messageApi, contextHolder] = message.useMessage();

    const warning = (msg: string) => {
        messageApi.warning(msg);
    }

    const onFinish = (values: { username: string, password: string }) => {
        const {username, password} = values;

        const msg = validLoginForm(username, password);
        if (msg != undefined) {
            warning(msg);
            return;
        }

        // 发送登陆请求
        login({username, password}).then(response => {
            const id = response.data.rootFolderId;
            navigate(`/home/${id}`);
        }).catch(error => {
            warning(error.message);
        })
    };

    const onFinishFailed = (errorInfo: any) => {
        console.log('Failed:', errorInfo);
    };
    return (
        <div className={styles["login-container"]}>
            {contextHolder}
            <div className={styles["login-form"]}>
                <span className={styles["title"]}> Mist Cloud</span>
                <Form className={styles["form"]}
                      name="basic"
                      labelCol={{span: 8}}
                    // wrapperCol={{span: 16}}
                      style={{maxWidth: 400}}
                      initialValues={{remember: true}}
                      onFinish={onFinish}
                      onFinishFailed={onFinishFailed}
                      autoComplete="off"
                >
                    <Form.Item<FieldType>
                        label="用户名"
                        name="username"

                    >
                        <Input/>
                    </Form.Item>

                    <Form.Item<FieldType>
                        label="密码"
                        name="password"
                    >
                        <Input.Password/>
                    </Form.Item>

                    <Form.Item wrapperCol={{offset: 8, span: 15}}>
                        <Button type="primary" htmlType="submit">
                            登陆
                        </Button>
                    </Form.Item>
                </Form>
            </div>

        </div>
    )
}