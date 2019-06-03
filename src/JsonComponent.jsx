import React from 'react';
import ReactDOM from 'react-dom'
import "./JsonComponent.css"
import XtextServices from "./ServerConnection/xtextServices";
import {mxClient, mxGraph, mxUtils, mxGraphView} from "mxgraph-js"
class JsonComponent extends React.Component{
    constructor() {
        super()
        var container = ReactDOM.findDOMNode(this.refs.mxgraphDiv);
        //const graph = new mxGraph(container);
        const graph = new mxGraph();
        var container2 = ReactDOM.findDOMNode(this.refs.mxgraphDiv2);
        //const graph2 = new mxGraph(container2);
        const graph2 = new mxGraph();
        this.state={
            graph:graph,
            graph2:graph2
        }
        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                const {graph}=this.state;
                let parent=graph.getDefaultParent();
                let x=Math.random()*320;
                let y=Math.random()*240;
                graph.insertVertex(parent, null, 'JSON', x, y, 40, 20);
                console.log("constructor success listener")
                this.setState({graph:graph});
            }
        });
    }

    componentDidMount=()=>{
        console.log("component did mount")
        //var container = ReactDOM.findDOMNode(this.refs.mxgraphDiv);

        if (!mxClient.isBrowserSupported()) {
            // Displays an error message if the browser is not supported.
            mxUtils.error("Browser is not supported!", 200, false);
          } else {
            //const graph_clone=Object.assign(this.state.graph);
            const {graph} = this.state;
            //const graph = new mxGraph(container);
            var parent = graph.getDefaultParent();
            graph.getModel().beginUpdate();
             try{
                console.log(graph.getModel().updateLevel)
                var v1 = graph.insertVertex(parent, null, 'Hello,', 20, 20, 80, 30);
                var v2 = graph.insertVertex(parent, null, 'World!', 200, 150, 80, 30);
                graph.insertEdge(parent, null, '', v1, v2); 
             }
             finally{
                graph.getModel().endUpdate();
                console.log(graph.getModel().updateLevel)
             }


             var container2 = ReactDOM.findDOMNode(this.refs.mxgraphDiv2);
             const graph2_model=Object.assign(graph.getModel());
             //const graph2 = new mxGraph(container2);
             const graph2 = new mxGraph(container2, graph2_model);
             //this.setState({graph}) //CAUTION! calls render()
          }
    }

    render(){
        console.log("render")
        const {graph, graph2}=this.state;
        var container = ReactDOM.findDOMNode(this.refs.mxgraphDiv);
        const graph_new = new mxGraph(container, graph.getModel());
        graph_new.getModel().beginUpdate();
        try{ 
            console.log("graph_new")
            console.log(graph_new.getModel())
        }
        finally{
            graph_new.getModel().endUpdate();
        }



       // graph2.getModel().clear(); //empty graph2
       // graph2.addCells(graph.cloneCells(graph.getChildCells(graph.getDefaultParent()))); //add all cells from graph to graph2
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
        return (<div>
            <div className="container" ref="mxgraphDiv" > </div>
            <div className="container2" ref="mxgraphDiv2" > </div>
        </div>

        );
    }
}
export default JsonComponent;
