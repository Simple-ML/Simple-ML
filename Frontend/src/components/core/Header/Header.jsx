//node_module
import React from "react";
//style
import headerStyle from "./header.module.scss";
//images
import logo from "./../../../images/logo/logo.png";
import logoVector from "./../../../images/logo/logo-vector.svg";
import simpleMl from "./../../../images/logo/simple-ml.svg";

class Header extends React.Component{
    render() {
        return(
            <div className={headerStyle["app-header"]}>
                <div className={headerStyle["logo-container"]}>
        {/* TODO: Logo entfernen oder behalten
        <img src={ logoVector } alt={ logo } className={headerStyle["logo"]}></img>*/}
                    <img src={ simpleMl } alt={ "SIMPLE ML" } className={headerStyle["simple-ml"]}></img>
                </div>
                <div className={headerStyle["button-container"]}>
                    {
                        this.props.children
                    }
                </div>
            </div>
        )
    }
}

export default Header;
