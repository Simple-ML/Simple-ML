//node_modules
import React from 'react';

import TextField from '@material-ui/core/TextField';
import FormControl from '@material-ui/core/FormControl';
import Button from '@material-ui/core/Button';
import  InputAdornment from '@material-ui/core/InputAdornment';
//React.Components
import LoginHeader from './LoginHeader/LoginHeader'
import SmlSwitch from "./../core/SmlSwitch/SmlSwitch"
import userIcon from "./../../images/login/user-default-grey.svg"

//style
import './loginView.scss'
import background from './../../styles/background.module.scss'


class LoginView extends React.Component {
    constructor() {
        super();
        this.state = {
        };
    }

    forgotPaswort = () => {
        return;
    }
    render() {
        var placeholder={
            username: "Nutzername",
            password: "Passwort",
            remember: "Passwort merken",
            forgot: "Passwort vergessen?",
            login: "Anmelden"
        }
        return(
            <div className='LoginView'>
                <LoginHeader />
                <div className={`login-container ${background.geometry}`}>
                <FormControl>
                    <TextField className="username-input" placeholder={placeholder.username} id="input-with-icon-textfield" variant="outlined" fullWidth="true"     
                        startAdornment={
                            <InputAdornment position="start">
                                <img src={ userIcon } className="user-icon"></img>
                            </InputAdornment>
                        }>
                    </TextField>
                    <TextField className="password-input" placeholder={placeholder.password} id="input-with-icon-textfield" variant="outlined"  
                    startAdornment={
                        <InputAdornment position="start">
                            <img src={ userIcon } className="user-icon"></img>
                        </InputAdornment>
                    }></TextField>
                    <div className="remember-form">
                        <SmlSwitch onChange={this.forgotPaswort} value={ "false" } color="default" className="switch-icon"/>
                        <div className="switch-label">{placeholder.remember}</div>
                        <a href="url" className="forgot-link">{placeholder.forgot}</a>
                    </div>


                    <Button variant="contained" className="orange-button" disableRipple> {placeholder.login}</Button>
                </FormControl>
                </div>
            </div>
        )
    }
}

export default LoginView;