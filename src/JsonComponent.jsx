import React from 'react';
import ReactDOM from 'react-dom'
import "./JsonComponent.css"
import XtextServices from "./ServerConnection/xtextServices";
import {mxClient, mxGraph, mxUtils} from "mxgraph-js"
class JsonComponent extends React.Component{
    constructor() {
        super()
        var container = ReactDOM.findDOMNode(this.refs.mxgraphDiv);
        const graph = new mxGraph(container);
        const graph2 = new mxGraph(container);
        this.state={
            graph:graph,
            clone:graph2
        }
        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                const {graph}=this.state;
                let parent=graph.getDefaultParent();
                var v1 = graph.insertVertex(parent, null, 'JSON', 50, 50, 100, 100);
                //this.setState({graph:graph});
            }
        });
    }

    componentDidMount=()=>{
        var container = ReactDOM.findDOMNode(this.refs.mxgraphDiv);
        if (!mxClient.isBrowserSupported()) {
            // Displays an error message if the browser is not supported.
            mxUtils.error("Browser is not supported!", 200, false);
          } else {
            const graph_clone=Object.assign(this.state.graph);
            const {graph} = this.state;
            //const graph = new mxGraph(container);
            var parent = graph.getDefaultParent();
            graph.getModel().beginUpdate();
             try{
                console.log(graph.getModel().updateLevel)
                var v1 = graph.insertVertex(parent, null, 'Hello,', 20, 20, 80, 30);
                var v2 = graph.insertVertex(parent, null, 'World!', 200, 150, 80, 30);
                var e1 = graph.insertEdge(parent, null, '', v1, v2);
        
             }
             finally{
                graph.getModel().execute();
                graph.getModel().endUpdate();
                console.log(graph.getModel().updateLevel)
             }
             this.setState({graph})
             // this.setState({clone:graph_clone})
             console.log(this.state)
             console.log(graph_clone)
          }
    }

    render(){
        const {graph, clone}=this.state;
        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                console.log("render");
                console.log(graph);
                console.log("clone")
                console.log(clone);
            }
        });
        return (
            <div className="container" ref="mxgraphDiv" > </div>
        );
    }
}
export default JsonComponent;
