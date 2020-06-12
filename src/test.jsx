import { mxUtils, mxEvent, mxGraph, mxConnectionHandler, mxImage,  mxHierarchicalLayout, mxGraphView, mxTemporaryCellStates } from "mxgraph-js";
import { connect } from 'react-redux';
import React from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import ProcessCall from './emfMetadata/GenericProcessCall/GenericProcessCall';

class GraphicalEditorTest extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            graph: undefined
        }
        this.graphRef = React.createRef();
    }

    componentDidMount() {
        let container = ReactDOM.findDOMNode(this.graphRef.current);

        let graph = new mxGraph(container);
        graph.containerWidth = container.clientWidth;
        graph.containerHeight = container.clientHeight;
        graph.centerX = graph.containerWidth/2;
        graph.centerY = graph.containerHeight/2;
        graph.setHtmlLabels(true);
        graph.doResizeContainer(graph.containerWidth-20, graph.containerHeight-75)
        // disable default mxCellEditor
        if(graph.cellEditor) {
            graph.cellEditor.startEditing = () => {};
        }
        var parent = graph.getDefaultParent();
        
        graph.getLabel = function(cell)
        {
            return cell.contentDiv;
        }

        graph.isHtmlLabel = function(cell)
        {
            return true;
        }
        this.setState({ graph: graph })
    }

    componentDidUpdate() {
        if(this.props.emfModelFlat === undefined || this.props.emfModelFlat.length === 0)
            return;
        const graph = this.state.graph;
        // Adds cells to the model in a single step
        graph.getModel().beginUpdate();
        try
        {
            // graph.getModel().beginUpdate();
            // const reactComponent = <ProcessCall emfEntity={this.props.emfModelFlat[6]} globalConfig={{}} graphConfig={{}} />;
            // const reactComponent = <div style={{height:100, width:100, background:"red"}}></div>
            const reactComponent = new ProcessCall({
                emfEntity: this.props.emfModelFlat[6],
                globalConfig: {},
                graphConfig: {}
            });

            reactComponent.renderToMxGraph(graph);

            var newDiv = document.createElement("div");
            
            // var vertex = graph.insertVertex(graph.getDefaultParent(), null, '', 200, 150, 80, 30);
            // vertex.fromReactComponent = newDiv;
            // ReactDOM.render(reactComponent, newDiv).renderToMxGraph(graph);
        }
        finally
        {
            // Updates the display
            graph.getModel().endUpdate();
        }
    }

    render() {
        return (
            <div>
                <div className={`graphicalEditor`} style={{height:"1000px"}} ref={this.graphRef}></div>
                <div id={'mxReactPlaceholder'}></div>
            </div>
        );
    }
}


GraphicalEditorTest.propTypes = {
    emfModelFlat: PropTypes.array.isRequired
};

const mapStateToProps = state => {
    return {
        emfModelFlat: state.emfModel.emfModelFlat
    }
};

const mapDispatchToProps = dispatch => {
    return {
        
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(GraphicalEditorTest);
