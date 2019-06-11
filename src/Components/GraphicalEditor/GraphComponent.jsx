
import React from 'react';
import ReactDOM from 'react-dom'
import XtextServices from "../../ServerConnection/xtextServices";
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
        return (
            <div className={this.props.name} ref="graphDiv"> </div>
        );
    }
}
export default GraphComponent;
