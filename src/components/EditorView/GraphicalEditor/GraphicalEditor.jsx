//node_modules
import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { connect } from "react-redux";
import { mxClient, mxUtils, mxConstants } from "mxgraph-js";

import store from '../../../reduxStore';

//services
import XtextServices from "../../../serverConnection/XtextServices";

//classes
import SMLGraph from "./SMLgraph"

//helper
import EmfModelHelper from "../../../helper/EmfModelHelper";
import { EditorContext } from "./../../../helper/goldenLayoutServices/appContext"

class GraphicalEditor extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            graph: undefined,
            emfModelFlat: undefined,
            viewMode: mxConstants.DIRECTION_WEST
        };
        this.graphRef = React.createRef();
        this.viewMode = mxConstants.DIRECTION_WEST;

        store.subscribe(() => {
            let state = store.getState();
            this.setState({
                emfModelFlat: state.mxGraph.emfModelFlat,
                viewMode: state.mxGraph.viewMode
            })
        });
    }
    static contextType = EditorContext;

    componentDidMount() {
        let container = ReactDOM.findDOMNode(this.graphRef.current);
        if (!mxClient.isBrowserSupported()) {
            // Displays an error message if the browser is not supported.
            mxUtils.error("Browser is not supported!", 200, false);
        } else {
            let graph = new SMLGraph(container);
            graph.addGraphListeners();
            this.setState({ graph: graph })
        }
    }

    /**
     * @param {boolean} isVertical
     * @returns mxConstants
     */
    getViewMode = (isVertical) => {
        if (isVertical === "true") {
            return mxConstants.DIRECTION_NORTH;
        }
        else {
            return mxConstants.DIRECTION_WEST;
        }
    }

    /**
     * @param {Object} result: result in response from xtextServices
     */
    updateView = (emfModelFlat) => {
        let { graph, viewMode } = this.state;
        graph.clear();
        graph.initView(viewMode);
        graph.updateEMFModel(emfModelFlat);
        graph.render();
    };



    render() {
        if(this.state.graph !== undefined)
            this.updateView(this.state.emfModelFlat);

        let getViewMode = (mode) => this.getViewMode(mode);
        return (
            <div className={this.props.name} ref={this.graphRef}></div>
        );
    }
}

/*
<EditorContext.Consumer>
{value => {
    this.viewMode = getViewMode(value)
    return (<div className={this.props.name} ref={this.graphRef}>
    </div>)
}}
</EditorContext.Consumer>
*/

const mapStateToProps = state => {
    return {
        emfModelFlat: state.emfModelFlat
    }
};



export default connect(mapStateToProps)(GraphicalEditor);
