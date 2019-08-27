//node_modules
import React from 'react';
import ReactDOM from 'react-dom';
import store from '../../../reduxStore';
import ReactReduxComponent from '../../../helper/ReactReduxComponent';
import { mxClient, mxUtils } from "mxgraph-js";
//classes
import SMLGraph from "./SMLgraph"

class GraphicalEditor extends ReactReduxComponent {

    constructor(props) {
        super(props, () => {
            let state = store.getState();
            this.setState({
                emfModelFlat: state.emfModel.emfModelFlat,
                viewMode: state.graphicalEditor.viewMode
            })
        });
        this.graphRef = React.createRef();
    }

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

        return (
            <div className={this.props.name} ref={this.graphRef}></div>
        );
    }
}

export default GraphicalEditor;
