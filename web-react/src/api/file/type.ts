import CommonResponse from "@/api/type.ts";

export interface FolderDetailResponse extends CommonResponse {
    data: FolderDetail
}

export interface FolderDetail {
    name: string,
    path: PathItem[],
    fileList: File[]
}


export interface PathItem {
    id: string,
    name: string
}

export interface File {
    id: string,
    name: string,
    modifyTime: number
    size: number,
    isFolder: boolean
}

