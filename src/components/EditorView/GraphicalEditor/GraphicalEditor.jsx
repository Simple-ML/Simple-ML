//node_modules
import React from 'react';
import ReactDOM from 'react-dom';
import { mxClient, mxUtils, mxConstants } from "mxgraph-js";

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
            graph: '',
        }
        this.graphRef = React.createRef();
        this.viewMode = mxConstants.DIRECTION_WEST;
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
            this.addXtextServiceListeners();
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
    updateView = (result) => {
        let { graph } = this.state;
        let viewMode = this.viewMode;
        graph.clear();
        graph.initView(viewMode);
        let flatModel = EmfModelHelper.flattenEmfModelTree(JSON.parse(result.emfModel));
        graph.updateEMFModel(flatModel);
        graph.render();
        this.setState({ graph: graph })
    }

    addXtextServiceListeners = () => {
        XtextServices.addSuccessListener((serviceType, result) => {
            switch (serviceType) {
                case 'getEmfModel':
                case 'deleteEntity':
                case 'deleteAssociation':
                case 'createAssociation':
                    this.updateView(result);
                    break;
                default:
                    break;
            }
        });
    }

    render() {
        let getViewMode = (mode) => this.getViewMode(mode);
        return (
            <EditorContext.Consumer>
                {value => {
                    this.viewMode = getViewMode(value)
                    return (<div className={this.props.name} ref={this.graphRef}>
                    </div>)
                }}
            </EditorContext.Consumer>
        );
    }
}
export default GraphicalEditor;
