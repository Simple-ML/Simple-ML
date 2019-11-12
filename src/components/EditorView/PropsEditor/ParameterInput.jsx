import React from 'react';
import {Tooltip, TextField} from '@material-ui/core'

class ParameterInput extends React.Component {
    constructor(props){
        super(props);
        this.state={
            name: props.name,
            type: props.type,
            value:props.value
        }
    } 

    handleChange(){
        console.log("text changed")
    }
    
    render(){
    let {key, name, type, value} = this.state;
    let tooltipText = "type: " + type;
    let handleChange= this.handleChange.bind(this);
        return(
            <div>
                <div >
                    {name}
                </div>
                <Tooltip title={tooltipText} placement="right">
                        <TextField fullWidth onChange={handleChange} value={value} variant="outlined" margin="dense"/>
                </Tooltip>
            </div>
        )
    }
}
export default ParameterInput;