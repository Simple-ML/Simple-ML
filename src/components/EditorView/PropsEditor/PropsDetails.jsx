//node_module
import React from 'react';
import mxGraphConfig from "./../GraphicalEditor/mxGraphConfig";
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import XtextServices from '../../../serverConnection/XtextServices';
import ParameterInput from './ParameterInput'
import PropsDetailsStyle from './propsDetails.module.scss';
import {Tooltip, TextField} from "@material-ui/core"

class PropsDetails extends React.Component {
    constructor(props){
        super(props);
        XtextServices.getProcessProposals();
        this.state={
            constants:{
                PROCESSCALL: "ProcessCall",
                ASSIGNMENT: "Assignment"
            }
        } 
    }

    createProcessCallComponentFragment(additionalInfo){
        var parameters = [];
        let children = this.props.context.value.children;
        if(additionalInfo[0]){
            var name = additionalInfo[0].name||"none";
            var namespace = additionalInfo[0].namespace||"none";
            var description = additionalInfo[0].description||"no description";
            var parameters = additionalInfo[0].parameters||[];
            var returns = additionalInfo[0].returns||"undefined";
            var infotext = "Please define the properties for the process"
        }
        let configs = this.props.context.value.children.filter(child => child.self === "config")
        console.log(configs)
        return (
            <React.Fragment>
                <div className={PropsDetailsStyle.title}>{name}</div>
                <div className={PropsDetailsStyle.infotext}>{parameters ? infotext: ""}</div>
                <div>{parameters.map((parameter, index) => 
                    (
                       <ParameterInput key={parameter.name} name={parameter.name} type={parameter.type} value={children[index].getValue()}></ParameterInput>
                    )
                )}</div>
                <div style={{display:configs.length !== 0? "block" : "none"}}>
                    <div>{ "configuration: \n"}</div>
                    <Tooltip title={"type: Dictionary"} placement="right">
                        <TextField multiline fullWidth variant="outlined" margin="dense" value={configs[0] !== undefined? configs[0].getValue() : ""} visibility={configs[0] !== undefined? "visible" : "hidden"}></TextField>
                    </Tooltip>
                </div>
            </React.Fragment>
        )
    }
    render(){
        let {constants} = this.state;
        let data = this.props.context.value.data;
        let className = data.className;
        let type = className.replace(mxGraphConfig.dslPrefix, "");
        let additionalInfo;
        let details;

        switch (type){
            case constants.PROCESSCALL:
                additionalInfo = this.props.processConfigs.filter(process => process.name === data.ref);
                details = this.createProcessCallComponentFragment(additionalInfo);
                break;    
            case constants.ASSIGNMENT:
                break;    
            default:
                break;    
        }
        return(
            <div>
                {details}
            </div>
        )
    }
}

PropsDetails.propTypes = {
    context: PropTypes.object.isRequired,
    processConfigs: PropTypes.array.isRequired
};

const mapStateToProps = state => {
    return {
        context: state.propsEditor.context,
        processConfigs: state.dslProcessDefinitions.processDefinitions
    };
};
export default connect(mapStateToProps)(PropsDetails);