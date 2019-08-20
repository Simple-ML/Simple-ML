//node_modules
import React, {useRef} from 'react';
import ReactDOM from 'react-dom';
import { mxClient, mxGraph, mxUtils, mxHierarchicalLayout,mxConnectionHandler, mxImage, mxConstants } from "mxgraph-js";
//services
import {AppContext} from "./../../../helper/goldenLayoutServices/appContext"
import XtextServices from "../../../serverConnection/XtextServices";
import MxGraphModelServices from './mxGraphModelServices';
import MxGraphConfig from "./mxGraphConfig";
import connectImage from "../../../images/arrow.png"
class GraphicalEditor extends React.Component {
    constructor(props) {
        super(props);
        let inputRef = useRef("graphDiv");
        this.state={
            graph: '',
            connectImage:connectImage,
            viewMode: mxConstants.DIRECTION_WEST,
            inputRef:inputRef
        }
    }

    componentDidMount() {
        let container = ReactDOM.findDOMNode(this.refs.graphDiv);
        var config = new MxGraphConfig();
        var graphService = new MxGraphModelServices();
        mxConnectionHandler.prototype.connectImage = new mxImage(connectImage,10,10);
        mxConnectionHandler.prototype.moveIconFront=true;
        if (!mxClient.isBrowserSupported()) {
            // Displays an error message if the browser is not supported.
            mxUtils.error("Browser is not supported!", 200, false);
        } else {
    
            let graph = new mxGraph(container);
            graphService.addAllListeners(graph);
            XtextServices.addSuccessListener((serviceType, result) => {

                //define mxgraphservices and configure layout;
                let parent = graph.getDefaultParent();
                var layout = new mxHierarchicalLayout(graph, this.getViewMode());
                console.log(layout);
                layout.intraCellSpacing = 20;
                graph.htmlLabels = true;
                graph.setConnectable(true);
                graphService.labelDisplayOveride(graph);

                switch(serviceType){
                    case 'getEmfModel':
                        //clear the graph.view
                        graph.removeCells(graph.getChildCells(parent, true, true));
                        //add nodes array to graph.view
                        graph.getModel().beginUpdate();
                        try {
                            graphService.renderFullText(result.emfModel, parent, graph, config);
                            layout.execute(parent);
                        }
                        finally {
                            graph.getModel().endUpdate();
                        }
                        this.setState({ graph: graph }); 
                        break;
                    case 'deleteEntity':
                    case 'deleteAssociation':
                    case 'createAssociation':  
                        XtextServices.getEmfModel();
                        break;
                    default:
                        break;
                }
            });
                this.setState({ graph: graph, connectImage:mxConnectionHandler.prototype.connectImage  });
        }

    }
    getViewMode=()=>{
        //console.log(this.props.isVertical )
        if(this.props.isVertical === "true"){
            this.setState({viewMode: mxConstants.DIRECTION_NORTH})
        }
        else{
            this.setState({viewMode: mxConstants.DIRECTION_WEST})
        }
        //console.log(this.state.viewMode)
        return this.state.viewMode
    }

    setValue = e => {
        console.log(e)
        this.setState({ value: e.target.value });
    }
    render() {
        let {inputRef}=this.state;
        return(
        <AppContext.Consumer>
           {value=> {return <div className={ this.props.name } isVertical={value} ref={inputRef}> 
            </div>}} 
        </AppContext.Consumer>
        );
    }
}
export default GraphicalEditor;
