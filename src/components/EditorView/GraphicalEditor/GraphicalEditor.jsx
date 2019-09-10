//node_modules
import React from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { mxClient, mxUtils } from "mxgraph-js";
//classes
import SMLGraph from "./SMLGraph"
//styles
import background from './../../../styles/background.module.scss'

class GraphicalEditor extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            graph: undefined
        }
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
    updateView = () => {
        let { graph } = this.state;
        let { emfModelFlat, viewMode } = this.props
        graph.clear();
        graph.initView(viewMode);
        graph.updateEMFModel(emfModelFlat);
        graph.render();
    };

    render() {
        if(this.state.graph !== undefined)
            this.updateView();

        return (
            <div className={`graphicalEditor ${background.darkCircles} ${this.props.name}`} ref={this.graphRef}></div>
        );
    }
}

GraphicalEditor.propTypes = {
    emfModelFlat: PropTypes.array.isRequired,
    viewMode: PropTypes.string.isRequired
};

const mapStateToProps = state => {
    return {
        emfModelFlat: state.emfModel.emfModelFlat,
        viewMode: state.graphicalEditor.viewMode
    };
};

export default connect(mapStateToProps, null)(GraphicalEditor);
