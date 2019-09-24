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
import graphicalEditorStyle from './graphicalEditor.module.scss';
import createButtonIcon from '../../../images/graph/plus.svg';
//redux
import './toolbar.inference';
import './createButton.inference';
import { openToolbar } from '../../../reducers/toolbar';
import { createButtonVerificationToken } from './createButton.inference';



class GraphicalEditor extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            graph: undefined
        }
        this.graphRef = React.createRef();
        this.createButtonRef = React.createRef();

        this.createButtonClick = this.createButtonClick.bind(this);
    }

    createButtonClick = () => {
        let {x, y} = ReactDOM.findDOMNode(this.createButtonRef.current).getBoundingClientRect();
        this.props.createEmfEntity(createButtonVerificationToken, x, y)
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
            <div className={graphicalEditorStyle.graphicalEditorContainer} >
            <div className={`graphicalEditor ${background.darkCircles} ${this.props.name}`} ref={this.graphRef}></div>
                <input
                    ref={this.createButtonRef}
                    type={'image'} src={createButtonIcon}
                    className={graphicalEditorStyle["graphical-editor-create-button"]}
                    onClick={this.createButtonClick}
                    disabled={this.props.dirty}
                >
                </input>
            </div>
        );
    }
}

GraphicalEditor.propTypes = {
    emfModelFlat: PropTypes.array.isRequired,
    viewMode: PropTypes.string.isRequired,
    dirty: PropTypes.bool.isRequired
};

const mapStateToProps = state => {
    return {
        emfModelFlat: state.emfModel.emfModelFlat,
        viewMode: state.graphicalEditor.viewMode,
        dirty: state.emfModel.dirty
    };
};

const mapDispatchToProps = dispatch => {
    return {
        createEmfEntity: (context, posX, posY) => dispatch(openToolbar(context, posX, posY))
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(GraphicalEditor);
