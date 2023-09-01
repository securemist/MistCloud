import styles from "./app-main.module.scss";
import {Route, Routes} from "react-router-dom";
import {Home} from "@/pages/home";
import {Recycle} from "@/pages/recycle";
import React from "react";

export function Layout() {
    return (
        <div className={styles.main}>
            <div className={styles.content}>
                <Routes>
                    <Route path={"/home/:id"} element={<Home/>}>
                    </Route>
                    <Route path={"/recycle"} element={<Recycle/>}/>
                </Routes>
            </div>
        </div>
    )
}