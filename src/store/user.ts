import {create} from "zustand";
import {persist} from "zustand/middleware";
import {removeToken, saveToken} from "@/utils/auth.ts";
import {TokenInfo} from "@/api/user/type.ts";
import {history} from "@/utils/history.ts";

export interface UserStore {
    folderId: string;
    iconView: boolean;
    checkView: () => void;
    setFolderId: (id: string) => void;
    login: (id: string, tokenInfo: TokenInfo) => void;
    logout: () => void;
}

export const useUserStore = create(persist<UserStore>((set, get) => ({
    iconView: true,
    folderId: "",
    setFolderId: (id: string) => {
        set(state => ({folderId: id}));
    },
    checkView: () => {
        set(state => ({iconView: !get().iconView}));
    },
    login: (id: string, tokenInfo: TokenInfo) => {
        set(state => ({folderId: id}));
        saveToken(tokenInfo);
    },
    logout: () => {
        removeToken();
    }
}), {
    name: "userConfig",
}))