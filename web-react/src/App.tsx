import {HashRouter as Router, Outlet, Route, Routes} from "react-router-dom";
import React from "react";
import {Login} from "@/pages/login";
import {Home} from "@/pages/home";
import {Recycle} from "@/pages/recycle";
import {Layout} from "@/layout";

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
    return (
        <>
            <Routes>
                <Route path={"/login"} element={<Login/>}/>
                <Route path={"/"} element={<Layout/>}>
                    <Route path={"home/:id"} element={<Home/>}/>
                    <Route path={"recycle"} element={<Recycle/>}/>
                </Route>
            </Routes>

        </>

    )
}

export default App

