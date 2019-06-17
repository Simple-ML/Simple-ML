//node_modules
import React from "react";
import Switch from '@material-ui/core/Switch';
import Grid from '@material-ui/core/Grid';
//style
import './EditorSwitch.scss';


class EditorSwitch extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            isGraphical: true,
        }
    }
    
    handleChange = name => event => {
        this.setState({ ...this.state, [name]: event.target.checked });
        this.props.switchView(event.target.value);
      };
    
    render() {
        let { isGraphical } = this.state;
        let handleChange = (name) => this.handleChange(name);
        return(
            <Grid component="label" container alignItems="center" spacing={1}>
                <span className="view-label"> View: </span>
                <Grid item style={{ color: isGraphical? '#7C7C7C': 'black' }}>graphical</Grid>
                <Grid item>
                    <Switch checked={ isGraphical } onChange={ handleChange('isGraphical') } value={ isGraphical } color="default" className="switch-icon"/>
                </Grid>
                <Grid item style={{ color: isGraphical? 'black': '#7C7C7C' }}>textual</Grid>
            </Grid>
        )
    }
}


export default EditorSwitch;