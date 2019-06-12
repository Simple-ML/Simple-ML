import React from "react";
import Switch from '@material-ui/core/Switch';
import Grid from '@material-ui/core/Grid';
import './EditorSwitch.scss'


class EditorSwitch extends React.Component{
    constructor(props){
        super(props)
        this.state={
            checked:true,
        }
    }
    handleChange = name => event => {
        console.log(this.state.checked)
        this.setState({ ...this.state, [name]: event.target.checked });
      };
    
    render() {
        let {checked}=this.state;
        let handleChange=(name)=>this.handleChange(name);
        return(
            <Grid component="label" container alignItems="center" spacing={1}>
                <span className="view-label">View: </span>
                <Grid item style={{color:checked? '#7C7C7C':'black'}}>graphical</Grid>
                <Grid item>
                    <Switch checked={checked} onChange={handleChange('checked')} value="checked" color="default" className="switch-icon"/>
                </Grid>
                <Grid item style={{color:checked? 'black':'#7C7C7C'}}>textual</Grid>
            </Grid>
        )

    }

}


export default EditorSwitch;