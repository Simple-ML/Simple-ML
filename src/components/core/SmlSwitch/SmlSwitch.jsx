//node_modules
import React from "react";
import Switch from '@material-ui/core/Switch';
//style
import './smlSwitch.scss';


class SmlSwitch extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            checked: false,
        }
    }

    handleChange = name => event => {
        this.setState({ ...this.state, [name]: event.target.checked });
        //this.props.switchView(event.target.value);
      };

    render() {
        let { checked } = this.state;
        let handleChange = (name) => this.handleChange(name);
        return(
            <Switch checked={ checked } onChange={ handleChange('checked') } value={ checked } color="default" className="switch-icon"/>
        )
    }
}


export default SmlSwitch;