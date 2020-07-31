import { mxUtils, mxEvent, mxGraph, mxConnectionHandler, mxImage,  mxHierarchicalLayout, mxGraphView, mxTemporaryCellStates, mxConstants } from "mxgraph-js";
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

        
        // // configureStylesheet(this);
        // // mxConnectionHandler.prototype.connectImage = new mxImage(connectImage,10,10);
        // // mxConnectionHandler.prototype.moveIconFront=true;
        
        graph.htmlLabels = true;
        graph.setConnectable(true);
        // graph.setCellsMovable(false);
        graph.setCellsResizable(false);

        // disable default mxCellEditor
        if(graph.cellEditor) {
            graph.cellEditor.startEditing = () => {};
        }
        
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
        const graph = this.state.graph;
        const parent = this.state.graph.getDefaultParent();
        let allPromises = [];
        
        let layout = new mxHierarchicalLayout(graph, mxConstants.DIRECTION_NORTH);
        layout.parentBorder = 50;
        layout.intraCellSpacing = 100;

        graph.removeCells(graph.getChildCells(parent, true, true))
        graph.getModel().beginUpdate();
        try
        {
            for(let entity of this.props.renderableEntities) {
                let reactComponent = new entity.metadata.mxGraphMetadata.component({
                    emfEntity: entity,
                    globalConfig: {},
                    graphConfig: {}
                });
                allPromises.push(reactComponent.renderToMxGraph(graph));
            }
            Promise.all(allPromises).then(() => {
                layout.execute(parent);
            })
        }
        finally
        {
            // Updates the display
            graph.getModel().endUpdate();
        }
    }

    updateLayout(graph) {
        
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
    renderableEntities: PropTypes.array.isRequired,
    entityAssociations: PropTypes.array.isRequired
};

const mapStateToProps = state => {
    return {
        renderableEntities: state.emfModel.renderable,
        entityAssociations: state.emfModel.associations
    }
};

const mapDispatchToProps = dispatch => {
    return {
        
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(GraphicalEditorTest);
