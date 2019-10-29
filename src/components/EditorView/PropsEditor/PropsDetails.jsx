//node_module
import React from 'react';
import mxGraphConfig from "./../GraphicalEditor/mxGraphConfig";
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import XtextServices from '../../../serverConnection/XtextServices';

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
        console.log(children)
        if(additionalInfo[0]){
            var name = additionalInfo[0].name||"none";
            var namespace = additionalInfo[0].namespace||"";
            var description = additionalInfo[0].description||"no description";
            var parameters = additionalInfo[0].parameters||[];
            var returns = additionalInfo[0].returns||"";
        }
        return (
            <React.Fragment>
                <li>Description: {description}</li>
                <li>Name: {name}</li>
                <li>Namespace: {namespace}</li>
                <ol>Parameters: {parameters.map((parameter, index) => 
                    (
                        <div key={parameter.name}>
                            <li key={parameter.name}> name: {parameter.name}; type: {parameter.type}</li>
                            <label>Value: </label>
                            <div>{children[index].getValue()} </div>
                        </div>
                    )
                )}</ol>
                <li>returns: {returns}</li>
            </React.Fragment>
        )
    }
    render(){
        let {constants} = this.state;
        let data = this.props.context.value.data;
        let className = data.className;
        let type = className.replace(mxGraphConfig.dslPrefix, "");
        let additionalInfo;
        let description;

        switch (type){
            case constants.PROCESSCALL:
                additionalInfo = this.props.processConfigs.filter(process => process.name === data.ref);
                description=this.createProcessCallComponentFragment(additionalInfo);
                break;    
            case constants.ASSIGNMENT:
                console.log("assignment");
                break;    
            default:
                console.log("default");
                break;    
        }
        return(
            <div>
                {type}
                {description}
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