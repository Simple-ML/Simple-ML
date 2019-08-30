//node_modules
import React from "react";
import SmlSwitch from "./../../core/SmlSwitch/SmlSwitch";
import Grid from '@material-ui/core/Grid';
//style
import './editorSwitch.scss';


class EditorSwitch extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            checked: true,
        }
    }

    handleChange = name => event => {
        this.setState({ ...this.state, [name]: event.target.checked });
        this.props.switchView(event.target.value);
      };

    render() {
        let { checked } = this.state;
        let handleChange = (name) => this.handleChange(name);
        return(
            <Grid component="label" container alignItems="center" spacing={1}>
                <span className="view-label"> View: </span>
                <Grid item style={{ color: checked? '#7C7C7C': 'black' }}>graphical</Grid>
                <Grid item>
                    <SmlSwitch checked={ checked } onChange={ handleChange('isGraphical') } value={ checked } color="default" className="switch-icon"/>
                </Grid>
                <Grid item style={{ color: checked? 'black': '#7C7C7C' }}>textual</Grid>
            </Grid>
        )
    }
}


export default EditorSwitch;
