//node_module
import React from "react";
//images
import logo from "./../../../images/logo/logo.png";
import logoVector from "./../../../images/logo/logo-vector.svg";
import simpleMl from "./../../../images/logo/simple-ml.svg";
//style
import "./header.scss";


class Header extends React.Component{
    render() {
        return(
            <div className="app-header">
                <img src={ logoVector } alt={ logo } className="logo"></img>
                <img src={ simpleMl } alt={ "SIMPLE ML" } className="simple-ml"></img>
            </div>
        )
    }
}

export default Header;