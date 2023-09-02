import styles from "./app-main.module.scss";

import {HashRouter as Router, Routes, Route, Outlet} from "react-router-dom"
import {Home} from "@/pages/home";
import {Recycle} from "@/pages/recycle";
import React from "react";
import {useUserStore} from "@/store/user.ts";

export function AppMain() {
    const id = useUserStore.getState().folderId;

    return (
        <div className={styles.main}>
            <div className={styles.content}>
                <Routes>
                    <Route path={`/home/:id`} element={<Home/>}>
                    </Route>
                    <Route path={"/recycle"} element={<Recycle/>}/>
                </Routes>
            </div>
        </div>
    )
}