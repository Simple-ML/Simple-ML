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
        graph.getModel().beginUpdate();
        graph.getModel().endUpdate();
        this.state={
            graph:graph,
            graph2:graph2
        }
        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                const {graph}=this.state;
                let parent=graph.getDefaultParent();
                var v1 = graph.insertVertex(parent, null, 'JSON', 50, 50, 100, 100);
                console.log("constructor success listener")
                this.setState({graph:graph});
            }
        });
    }

    componentDidMount=()=>{
        console.log("component did mount")
        var container = ReactDOM.findDOMNode(this.refs.mxgraphDiv);

        if (!mxClient.isBrowserSupported()) {
            // Displays an error message if the browser is not supported.
            mxUtils.error("Browser is not supported!", 200, false);
          } else {
            const graph_clone=Object.assign(this.state.graph);
            const {graph,graph2} = this.state;
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
                graph.getModel().endUpdate();
                console.log(graph.getModel().updateLevel)
             }
             //this.setState({graph}) //CAUTION! calls render()
          }
    }

    render(){
        console.log("render")
        const {graph, graph2}=this.state;
        graph2.getModel().clear(); //empty graph2
        graph2.addCells(graph.cloneCells(graph.getChildCells(graph.getDefaultParent()))); //add all cells from graph to graph2
        console.log("graph")
        console.log(graph.getModel());
        console.log("clone")
        console.log(graph2.getModel());
        /*XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                console.log("render success listener");
                graph2.addCells(graph.cloneCells(graph.getChildCells(graph.getDefaultParent())));

                console.log(graph);
                console.log("clone")
                console.log(graph2);
            }
        });*/
        return (
            <div className="container" ref="mxgraphDiv" > </div>
        );
    }
}
export default JsonComponent;
