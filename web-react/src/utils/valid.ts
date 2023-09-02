export const validLoginForm = (username: string, password: string): string | undefined => {
    if (stringEmpty(username) || stringEmpty(password)) {
        return "用户名或密码不能为空";
    }

}

export const validFolderName = (name: string): string | undefined => {
    if (stringEmpty(name)) {
        return "文件夹名称不能为空";
    }
}

const stringEmpty = (str: string): boolean => {
    return str == undefined || str == "" || str == null;
}