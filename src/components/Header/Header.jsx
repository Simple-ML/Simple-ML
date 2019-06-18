//node_module
import React from "react";
//images
import logo from "./../../images/logo.png";
//style
import "./header.scss";


class Header extends React.Component{
    render() {
        return(
            <div className="app-header">
                <img src={ logo } alt={ "logo" } className="logo"></img>
            </div>
        )
    }
}

export default Header;