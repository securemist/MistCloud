import {BrowserRouter as Router, Outlet, Route, Routes, useNavigate} from "react-router-dom";
import React, {useEffect} from "react";
import {Login} from "@/pages/login";
import {Home} from "@/pages/home";
import {Recycle} from "@/pages/recycle";
import {Layout} from "@/layout";
import {useUserStore} from "@/store/user.ts";
import {NotFound} from "@/pages/another/404.tsx";
import {getToken} from "@/utils/auth.ts";
import {stringEmpty} from "@/utils/common.ts";
import {history} from "@/utils/history.ts";

function App() {
    return (
        <>
            <Router>
                <Screen/>
            </Router>
        </>
    )
}

function Screen() {
    const id = useUserStore().folderId;
    const navigate = useNavigate();
    const token = getToken();

    useEffect(() => {
        if (stringEmpty(token)) {
            history.push("/login")
        }
    })

    return (
        <>
            <Routes>
                {}
                <Route path={"/login"} element={<Login/>}/>
                <Route path={"/"} element={<Layout/>}>
                    <Route path={"home/:id"} element={<Home/>}/>
                    <Route path={"recycle"} element={<Recycle/>}/>
                </Route>
                <Route path={"*"} element={<NotFound/>}/>
            </Routes>

        </>

    )
}

export default App

