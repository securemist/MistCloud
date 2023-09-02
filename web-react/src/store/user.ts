import {create} from "zustand";
import {devtools, persist} from "zustand/middleware";
import Pubsub from "pubsub-js";

export interface UserStore {
    folderId: string;
    iconView: boolean;
    checkView: () => void;

    setFolderId: (id: string) => void;
}


export const useUserStore = create(persist<UserStore>((set, get) => ({
    iconView: true,
    folderId: "",
    selectedFiles: new Set<string>(),
    setFolderId: (id: string) => {
        set(state => ({folderId: id}));
    },
    checkView: () => {
        set(state => ({iconView: !get().iconView}));
        console.log(1)
    },
}), {
    name: "userConfig",
}))