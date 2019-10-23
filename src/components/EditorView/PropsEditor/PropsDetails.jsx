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


    render(){
        let {constants} = this.state;
        let value = this.props.context.value;
        let data = value.data;
        let className = data.className;
        let type = className.replace(mxGraphConfig.dslPrefix, "");
        let additionalInfo;
        let description;

        switch (type){
            case constants.PROCESSCALL:
                additionalInfo = this.props.processConfigs.filter(process => process.name === data.ref);
                console.log(additionalInfo)
                description = React.createElement("div","name:")
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