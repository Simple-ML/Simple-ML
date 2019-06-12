
import React from 'react';
import ReactDOM from 'react-dom'
import "./Workflow.css"
import XtextServices from "./ServerConnection/xtextServices";
import { mxClient, mxGraph, mxUtils, mxHierarchicalLayout } from "mxgraph-js"
import GraphComponent from './GraphComponent';

class WorkflowComponent extends React.Component {
    constructor(props) {
        super(props)
        const graph = new mxGraph();
        this.state = {
            graph: graph
        }
    }
    render() {
        return (
           <div className="workflow-container">
               <GraphComponent/>
           </div>
        );
    }
}

export default WorkflowComponent;