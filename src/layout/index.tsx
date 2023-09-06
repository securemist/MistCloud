import styles from "./layout.module.scss";
import {Route, Routes} from "react-router-dom";
import {Home} from "@/pages/home";
import {Recycle} from "@/pages/recycle";
import React, {HTMLAttributes} from "react";
import {Login} from "@/pages/login";
import {Navbar} from "@/layout/navbar";
import {AppMain} from "@/layout/appmain";
import {Footer} from "@/layout/footer";
import {UploadList} from "@/components/UploadLIst";
export function Layout() {
    return (
        <div className={styles.container}>
            <Navbar/>
            <AppMain className={styles.app}/>
            <Footer/>
            <UploadList/>
        </div>
    )
}

const UploadListButton = (props: HTMLAttributes<any>) => {

}