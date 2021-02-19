import { 
    mxUtils, 
    mxEvent, 
    mxGraph, 
    mxEdgeHandler, 
    mxConnectionHandler, 
    mxImage, 
    mxHierarchicalLayout, 
    mxGraphView, 
    mxTemporaryCellStates, 
    mxPerimeter, 
    mxConstants 
} from "mxgraph-js";
import { connect } from 'react-redux';
import React from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';

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

        let graph = new mxGraph(container);
        graph.containerWidth = container.clientWidth;
        graph.containerHeight = container.clientHeight;
        graph.centerX = graph.containerWidth/2;
        graph.centerY = graph.containerHeight/2;
        graph.setHtmlLabels(true);
        graph.doResizeContainer(graph.containerWidth, graph.containerHeight);
        graph.setConnectable(true);
        this.initGraphStyle(graph);
        this.addLabelperimeter(graph);
        
        graph.htmlLabels = true;
        graph.setConnectable(true);
        graph.setCellsMovable(false);
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
        
        // TODO: do not remove all cells if rendering gets slow
        graph.removeCells(graph.getChildCells(parent, true, true))
        graph.getModel().beginUpdate();
        try
        {
            let allPromises = [];
            // create mxCells for all renderable entities
            for(let entity of this.props.renderableEntities) {
                let reactComponent = new entity.metadata.mxGraphMetadata.component({
                    emfEntity: entity,
                    globalConfig: {},
                    graphConfig: {}
                });
                allPromises.push(reactComponent.renderToMxGraph(graph));
            }
            Promise.all(allPromises).then(() => {
                let parentCells = graph.getChildCells(parent, true, false);
                let allCells = parentCells;
                let layout = new mxHierarchicalLayout(graph, mxConstants.DIRECTION_NORTH);
                layout.parentBorder = 50;
                layout.intraCellSpacing = 100;

                // flatten cell-list (entity-cells and port-cells in one array)
                for(let parentCell of parentCells) {
                    if(parentCell.children !== undefined && parentCell.children !== null && parentCell.children.length !== 0) {
                        allCells = allCells.concat(parentCell.children);
                    }
                }
                // create mxGraph.edges for all associations
                for(let association of this.props.entityAssociations) {
                    let sourceVertex = allCells.find((cell) => {
                        if(cell.emfReference.id === association.source.id)
                            return true;
                        else    
                            return false;
                    });
                    let targetVertex = allCells.find((cell) => {
                        if(cell.emfReference.id === association.target.id)
                            return true;
                        else    {
                            return false;
                        }
                    });
                    graph.insertEdge(parent, null, null, sourceVertex, targetVertex);

                    // apply layout
                    layout.execute(parent);
                }
            })
        }
        finally
        {
            // Updates the display
            graph.getModel().endUpdate();
        }
    }

    initGraphStyle(graph) {
        // vertex
        var style = new Object ();
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_GRADIENTCOLOR] = '#41B9F5';
        style[mxConstants.STYLE_FILLCOLOR] = '#8CCDF5';
        style[mxConstants.STYLE_STROKECOLOR] = '#1B78C8';
        style[mxConstants.STYLE_FONTCOLOR] = '#000000';
        style[mxConstants.STYLE_ROUNDED] = true;
        style[mxConstants.STYLE_OPACITY] = '80';
        style[mxConstants.STYLE_FONTSIZE] = '12';
        style[mxConstants.STYLE_FONTSTYLE] = 0;
        style[mxConstants.STYLE_IMAGE_WIDTH] = '48';
        style[mxConstants.STYLE_IMAGE_HEIGHT] = '48';
        graph.getStylesheet().putDefaultVertexStyle(style);
        // cell
        var style = new Object();
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
        style[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
        graph.getStylesheet().putCellStyle('TODO', style);
        // association
        var edgeStyle = graph.getStylesheet().getDefaultEdgeStyle();;
        edgeStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
        edgeStyle[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_OVAL;
        edgeStyle[mxConstants.STYLE_ROUNDED] = true;
        edgeStyle[mxConstants.STYLE_STROKEWIDTH] = "2";
        edgeStyle[mxConstants.STYLE_STROKECOLOR] = "#d8d8d8";
        edgeStyle[mxConstants.STYLE_ENDSIZE] = "3";
        edgeStyle[mxConstants.STYLE_TARGET_PERIMETER_SPACING]="5";
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);
    }

    addLabelperimeter(graph){
        // Redirects the perimeter to the label bounds if intersection
        // between edge and label is found
        var mxGraphViewGetPerimeterPoint = mxGraphView.prototype.getPerimeterPoint;
        mxGraphView.prototype.getPerimeterPoint = function(terminal, next, orthogonal, border) {
            var point = mxGraphViewGetPerimeterPoint.apply(this, arguments);
            
            if (point != null) {
                var perimeter = this.getPerimeterFunction(terminal);
                if (terminal.text != null && terminal.text.boundingBox != null) {
                    // Adds a small border to the label bounds
                    // TODO: get boundigBox of vertex not from text-lable
                    var b = terminal.text.boundingBox.clone();
                    b.grow(3);
                    if (mxUtils.rectangleIntersectsSegment(b, point, next)) {
                        point = perimeter(b, terminal, next, orthogonal);
                    }
                }
            }
            return point;
        };
    }

    render() {
        return (
            <div>
                <div className={`graphicalEditor`} style={{height:"320px"}} ref={this.graphRef}></div>
                <div id={'mxReactPlaceholder'}></div>
            </div>
        );
    }
}


GraphicalEditor.propTypes = {
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

export default connect(mapStateToProps, mapDispatchToProps)(GraphicalEditor);
