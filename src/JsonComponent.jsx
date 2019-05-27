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
