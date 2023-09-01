import type {FolderDetail} from "@/api/file/type.ts";
import {create} from "zustand";
import {devtools, persist} from "zustand/middleware";
import {getSubFoldersAndFiles} from "@/api/file";
import {useNavigate} from "react-router-dom";

export interface UserStore {
    folderId: string;

}

export const useUserStore = create<UserStore>()(
    persist(
        (set, get) => ({
            folderId: "",
        }),
        {
            name: "user",
        }
    )
)