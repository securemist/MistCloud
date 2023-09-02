import {create} from "zustand";
import {devtools, persist} from "zustand/middleware";

export interface UserStore {
    folderId: string;
    iconView: boolean;
    checkView: () => void;

    selectedFiles: Set<string>;
    addFile: (ids: string[]) => void;
    removeFile: (ids: string[]) => void;
    clearFiles: () => void;
}

const arr = ["sa", "asd"]
export const useUserStore = create<UserStore>((set, get) => ({
    folderId: "",
    iconView: false,
    selectedFiles: new Set<string>(),
    checkView: () => {
        set(state => ({iconView: !get().iconView}));
    },
    addFile: (ids: string[]) => {
        const set0 = get().selectedFiles;
        ids.forEach(id => {
            set0.add(id);
        })
        set(state => ({selectedFiles: set0}))
    },
    removeFile: (ids: string[]) => {
        const set0 = get().selectedFiles;
        ids.forEach(id => {
            set0.delete(id);
        })
        set(state => ({selectedFiles: set0}))
    },
    clearFiles: () => {
        set(state => ({selectedFiles: new Set<string>()}))
    }
}))

