//node_modules
import React from 'react';
import ReactDOM from 'react-dom';
import { mxClient, mxGraph, mxUtils, mxHierarchicalLayout,mxConnectionHandler, mxImage } from "mxgraph-js";
//services
import XtextServices from "../../../serverConnection/XtextServices";
import MxGraphModelServices from './mxGraphModelServices';
import MxGraphConfig from "./mxGraphConfig";

class GraphicalEditor extends React.Component {
    constructor(props) {
        super(props);
        const graph = new mxGraph();
        this.state = {
            graph: graph
        };
        XtextServices.addSuccessListener((serviceType, result) => {
            switch(serviceType){
               case 'getEmfModel':
                   {
                    let { graph } = this.state;

                    //define mxgraphservices and configure layout;
                    let parent = graph.getDefaultParent();
                    var config = new MxGraphConfig();
                    var graphService = new MxGraphModelServices();
                    var layout = new mxHierarchicalLayout(graph);
                    mxConnectionHandler.prototype.connectImage = new mxImage('/../../images/background-circle.svg');
                    layout.intraCellSpacing = 20;
                    graph.htmlLabels = true;
                    graph.setConnectable(true);
                    graphService.labelDisplayOveride(graph);
    
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
                   }
                case 'createAssociation':
                    {
                    let { graph } = this.state;
                        console.log(result);
                    break;
                    }
                default: break;    
            }
            
        });
    }

    componentDidMount() {
        let container = ReactDOM.findDOMNode(this.refs.graphDiv);

        if (!mxClient.isBrowserSupported()) {
            // Displays an error message if the browser is not supported.
            mxUtils.error("Browser is not supported!", 200, false);
        } else {
            let graph = new mxGraph(container);
            this.setState({ graph: graph });
        }

    }

    render() {
        return(
            <div className={ this.props.name } ref="graphDiv"> 
                                <button style={{ color: 'black' }} onClick={ () => { XtextServices.createAssociation("unconnected[0]/","unconnected[1]/"); }}>
                        { 'create association' }
                    </button>
            </div>
        );
    }
}
export default GraphicalEditor;
