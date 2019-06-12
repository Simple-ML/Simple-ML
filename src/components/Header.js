import React from "react";
import logo from "./../images/logo.png"
import "./Header.scss"


class Header extends React.Component{
    constructor(props){
        super(props)
        this.state={
            checked:true,
        }
    }
    
    render() {
        return(
            <div className="app-header">
             <img src={logo} alt={"logo"} className="logo"></img>
            </div>
        )

    }

}


export default Header;