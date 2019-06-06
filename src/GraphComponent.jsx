
import React from 'react';
import ReactDOM from 'react-dom'
import "./JsonComponent.css"
import XtextServices from "./ServerConnection/xtextServices";
import { mxClient, mxGraph, mxUtils, mxHierarchicalLayout } from "mxgraph-js"

import MxGraphModelServices from './mxGraphModelServices';
import MxGraphConfig from "./mxGraphConfig"


class GraphComponent extends React.Component {
    constructor(props) {
        super(props)
        const graph = new mxGraph();
        this.state = {
            graph: graph
        }
        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                console.log("constructor success listener")
                let { graph } = this.state;


                //define mxgraphservices and configure layout;
                let parent = graph.getDefaultParent();
                var config = new MxGraphConfig();
                var graphService = new MxGraphModelServices();
                var layout = new mxHierarchicalLayout(graph);
                layout.intraCellSpacing = 20;
                graph.htmlLabels = true;
                graphService.labelDisplayOveride(graph);

                //clear the graph.view
                graph.removeCells(graph.getChildCells(parent, true, true));
                //add nodes array to graph.view
                graph.getModel().beginUpdate();
                try {
                    graphService.renderFullText(result.fullText, parent, graph, config);
                    layout.execute(parent);
                }
                finally {
                    graph.getModel().endUpdate();
                }

                this.setState({ graph: graph });
            }
        });
    }

    componentDidMount = () => {
        console.log("component did mount")
        let container = ReactDOM.findDOMNode(this.refs.graphDiv);

        if (!mxClient.isBrowserSupported()) {
            // Displays an error message if the browser is not supported.
            mxUtils.error("Browser is not supported!", 200, false);
        } else {
            let graph = new mxGraph(container);
            let parent = graph.getDefaultParent();
            graph.getModel().beginUpdate();
            try {
                let v1 = graph.insertVertex(parent, null, 'Hello,', 20, 20, 80, 30);
                let v2 = graph.insertVertex(parent, null, 'World!', 200, 150, 80, 30);
                graph.insertEdge(parent, null, '', v1, v2);
            }
            finally {
                graph.getModel().endUpdate();
            }
            this.setState({ graph: graph });
        }
    }

    render() {
        console.log("render")
        return (
            <div className={this.props.name} ref="graphDiv"> </div>
        );
    }
}
export default GraphComponent;