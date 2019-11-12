import React from 'react';
import {Tooltip, TextField} from '@material-ui/core'
import propsEditorStyle from "./propsEditor.module.scss"

class ParameterInput extends React.Component {
    constructor(props){
        super(props);
        this.state = {
            name: props.name,
            type: props.type,
            value:props.value
        }
        this.handleChange = this.handleChange.bind(this);
    } 

    handleChange(){
        console.log("Editing paramenters is not implemented yet")
    }
    
    render(){
    let { name, type, value} = this.state;
    let tooltipText = "type: " + type;

        return(
            <div>
                <div className={propsEditorStyle.propLabel}>
                    {name}
                </div>
                <Tooltip title={tooltipText} placement="right">
                        <TextField fullWidth onChange={this.handleChange} value={value} variant="outlined" margin="dense"/>
                </Tooltip>
            </div>
        )
    }
}
export default ParameterInput;