import React from 'react';
import ReactDOM from 'react-dom'
import XtextServices from "./ServerConnection/xtextServices";
import {mxClient, mxGraph, mxUtils} from "mxgraph-js"
class JsonComponent extends React.Component{
    constructor() {
        super()
        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {

            }
        });
    }

    componentDidMount=()=>{
        var container = ReactDOM.findDOMNode(this.refs.mxgraphDiv);
        if (!mxClient.isBrowserSupported()) {
            // Displays an error message if the browser is not supported.
            mxUtils.error("Browser is not supported!", 200, false);
          } else {
            const graph = new mxGraph(container);
            var parent = graph.getDefaultParent();
            graph.getModel().beginUpdate();
             try{
                var v1 = graph.insertVertex(parent, null, 'Hello,', 20, 20, 80, 30);
                var v2 = graph.insertVertex(parent, null, 'World!', 200, 150, 80, 30);
                var e1 = graph.insertEdge(parent, null, '', v1, v2);
        
             }
             finally{
                graph.getModel().endUpdate();
             }
          }
    }

    render(){
        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                console.log("render");
            }
        });
        return (
            <div className="container" ref="mxgraphDiv"> </div>
        );
    }
}
export default JsonComponent;
