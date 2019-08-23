//node_modules
import React from 'react';
import ReactDOM from 'react-dom';
import { mxClient, mxUtils, mxConstants } from "mxgraph-js";
//services
import XtextServices from "../../../serverConnection/XtextServices";

import SMLGraph from "./SMLgraph"
//helper
import EmfModelHelper from "../../../helper/EmfModelHelper";

class GraphicalEditor extends React.Component {
    constructor(props) {
        super(props);
        this.state={
            graph: '',
            viewMode: mxConstants.DIRECTION_WEST
        }
    }


    componentDidMount() {
        let container = ReactDOM.findDOMNode(this.refs.graphDiv);
        if (!mxClient.isBrowserSupported()) {
            // Displays an error message if the browser is not supported.
            mxUtils.error("Browser is not supported!", 200, false);
        } else {
            let graph = new SMLGraph(container);
            //graph.initView(this.state.viewMode);
            this.setState({graph:graph})
            graph.addGraphListeners();
            this.addXtextServiceListeners();
        }

    }
    getViewMode=()=>{
        if(this.props.isVertical === "true"){
            this.setState({viewMode: mxConstants.DIRECTION_NORTH})
        }
        else{
            this.setState({viewMode: mxConstants.DIRECTION_WEST})
        }
        return this.state.viewMode
    }
    
    updateView =(result)=>{
        let {graph, viewMode}=this.state;
        console.log(viewMode);
        graph.clear();
        graph.initView(viewMode);
        let flatModel = EmfModelHelper.flattenEmfModelTree(JSON.parse(result.emfModel));
        graph.updateEMFModel(flatModel);
        graph.render();
        this.setState({graph:graph})
    }

    addXtextServiceListeners = () =>{
        XtextServices.addSuccessListener((serviceType, result) => {
            switch(serviceType){
                case 'getEmfModel':
                case 'deleteEntity':
                case 'deleteAssociation':
                case 'createAssociation': 
                    console.log(serviceType) 
                    this.updateView(result);
                    break;
                default:
                    break;
            }
        });
    }

    render() {
        return(
            <div className={ this.props.name } isVertical={this.props.isVertical} ref="graphDiv" > 
            </div>
        );
    }
}
export default GraphicalEditor;
