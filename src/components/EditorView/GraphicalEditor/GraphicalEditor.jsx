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
            let { graph } = this.state;

            //define mxgraphservices and configure layout;
            let parent = graph.getDefaultParent();
            var config = new MxGraphConfig();
            var graphService = new MxGraphModelServices();
            var layout = new mxHierarchicalLayout(graph);
            mxConnectionHandler.prototype.connectImage = new mxImage('../../../images/conncent.jpg',25,25);
            mxConnectionHandler.prototype.moveIconFront=true;
            layout.intraCellSpacing = 20;
            graph.htmlLabels = true;
            graph.setConnectable(true);
            graphService.labelDisplayOveride(graph);
            //clear the graph.view
            graph.removeCells(graph.getChildCells(parent, true, true));
            switch(serviceType){
               case 'getEmfModel':
                   {
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
                        graph.getModel().beginUpdate();
                        try {
                            graphService.renderFullText(result.emfModel, parent, graph, config);
                            layout.execute(parent);
                        }
                        finally {
                            graph.getModel().endUpdate();
                        }
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
            </div>
        );
    }
}
export default GraphicalEditor;
