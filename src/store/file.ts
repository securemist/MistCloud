import {create} from "zustand";

interface FileSetStore {
    selectedFiles: string[],
    getFileSet: () => Set<string>;
    addFile: (id:string) => void;
    removeFile: (id:string) => void;
    clearFiles: () => void;
}

export const useFileSelectedStore = create<FileSetStore>((set, get) => ({
    selectedFiles: [],
    getFileSet: () => {
        const fileSet = new Set<string>();
        get().selectedFiles.map(id => fileSet.add(id));
        return fileSet;
    },
    addFile: (id:string) => {
        const arr = get().selectedFiles;
        arr.push(id);
        set(state => ({selectedFiles:arr}));
    },
    removeFile: (id:string) => {
        const arr = get().selectedFiles;
        const arr0 = arr.filter(id0 => id0 !== id)
        set(state => ({selectedFiles:arr0}));
    },
    clearFiles: () => {
        set(state => ({selectedFiles:[]}));
    },
}))