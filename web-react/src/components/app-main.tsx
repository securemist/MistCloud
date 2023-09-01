import styles from "./app-main.module.scss";

import {HashRouter as Router, Routes, Route, Outlet} from "react-router-dom"
import {FilePage} from "@/components/file";
import {Recycle} from "@/components/recycle";
import React from "react";

export function AppMain() {
    return (
        <div className={styles.main}>
            <div className={styles.content}>
                <Routes>
                    <Route path={"/home/:id"} element={<FilePage/>}>
                    </Route>
                    <Route path={"/recycle"} element={<Recycle/>}/>
                </Routes>
            </div>

        </div>
    )
}