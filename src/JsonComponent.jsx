import React from 'react';
import ReactDOM from 'react-dom'
import "./JsonComponent.css"
import XtextServices from "./ServerConnection/xtextServices";
import { mxClient, mxGraph, mxUtils, mxCodec, mxGraphModel } from "mxgraph-js"
class JsonComponent extends React.Component {
    constructor() {
        super()
        var container = ReactDOM.findDOMNode(this.refs.mxgraphDiv);
        const graph = new mxGraph(container);
        var container2 = ReactDOM.findDOMNode(this.refs.mxgraphDiv2);
        const graph2 = new mxGraph(container2);
        this.state = {
            graph: graph,
            graph2: graph2,
            container: container,
            container2: container2
        }
        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                const { graph } = this.state;
                let parent = graph.getDefaultParent();
                let x = Math.random() * 320;
                let y = Math.random() * 240;
                graph.insertVertex(parent, null, 'JSON', x, y, 40, 20);
                console.log("constructor success listener")
                this.setState({ graph: graph });
            }
        });
    }

    componentDidMount = () => {
        console.log("component did mount")

        if (!mxClient.isBrowserSupported()) {
            // Displays an error message if the browser is not supported.
            mxUtils.error("Browser is not supported!", 200, false);
        } else {
            const { graph, container } = this.state;
            const graphOnMount = new mxGraph(container, graph.model)
            var parent = graphOnMount.getDefaultParent();
            graphOnMount.getModel().beginUpdate();
            try {
                var v1 = graphOnMount.insertVertex(parent, null, 'Hello,', 20, 20, 80, 30);
                var v2 = graphOnMount.insertVertex(parent, null, 'World!', 200, 150, 80, 30);
                graphOnMount.insertEdge(parent, null, '', v1, v2);
                console.log("graph_on_mount")
                console.log(graphOnMount.getModel())
            }
            finally {
                graphOnMount.getModel().endUpdate();
            }
        }
    }

    render() {
        var container = ReactDOM.findDOMNode(this.refs.mxgraphDiv);
        console.log("render")
        const { graph, graph2, container2 } = this.state;
        const graph_new = new mxGraph(container2, graph2.getModel());
        graph_new.getModel().beginUpdate();
        var parent = graph_new.getDefaultParent();
        try {
            var v1 = graph_new.insertVertex(parent, null, '2 Hello,', 20, 20, 80, 30);
            var v2 = graph_new.insertVertex(parent, null, '2 World!', 200, 150, 80, 30);
            var e1 = graph.insertEdge(parent, null, '', v1, v2);
            console.log("graph_new")
            console.log(graph_new.getModel())
        }
        finally {
            graph_new.getModel().endUpdate();
        }

        return (<div>
            <div className="container" ref="mxgraphDiv" > </div>
            <div className="container2" ref="mxgraphDiv2" > </div>
            <button className="jsonButton" >JSON-Serivce</button>
        </div>

        );
    }
}
export default JsonComponent;
