import type {FolderDetail} from "@/api/file/type.ts";
import {create} from "zustand";
import {persist} from "zustand/middleware";

export const useFolderStore = create<FolderDetail>(
)