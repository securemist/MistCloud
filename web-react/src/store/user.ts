import {create} from "zustand";
import {persist} from "zustand/middleware";

export interface UserStore {
    folderId: string;
    iconView: boolean;
    checkView: () => void;
    setFolderId: (id: string) => void;
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
}), {
    name: "userConfig",
}))