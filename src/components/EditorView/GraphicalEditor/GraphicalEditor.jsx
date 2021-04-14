import React from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { 
    mxUtils, 
    mxGraph, 
    mxHierarchicalLayout, 
    mxGraphView, 
    mxPerimeter,
    mxConstants
} from "mxgraph-js";
import { 
    entityHoverStateEnter, 
    entityHoverStateLeav,
    entitySelect,
    entityDeselect
} from '../../../reducers/graphicalEditor';
import {
    openContextMenu
} from '../../../reducers/contextMenu';

import './contextMenu.inference';

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
        
        graph.addMouseListener(
        {
            currentState: null,
            mouseMove: function(sender, me)
            {
                if (this.currentState != null && me.getState() === this.currentState) {
                    return;
                }

                var tmp = graph.view.getState(me.getCell());

                // Ignores everything but vertices
                if (graph.isMouseDown || (tmp != null && !graph.getModel().isVertex(tmp.cell))) {
                    tmp = null;
                }

                if (tmp !== this.currentState) {
                    if (this.currentState != null) {
                        this.dragLeave(me.getEvent(), this.currentState);
                    }

                    this.currentState = tmp;

                    if (this.currentState != null) {
                        this.dragEnter(me.getEvent(), this.currentState);
                    }
                }
            },
            mouseDown: (sender, me) => {
                const cell = me.getCell();
                
                if(cell && !cell.source && !cell.target) {
                    this.props.openContextMenu(cell, me.getX() + 10, me.getY() - 10);
                    this.props.entitySelect(cell.emfReference);
                } else {
                    this.props.entityDeselect();
                }
            },
            mouseUp: function(sender, me)
            {
                console.log('mouseUp');
            },
            dragEnter: (evt, state) =>  {
                this.props.entityHoverStateEnter(state.cell.emfReference);
            },
            dragLeave: (evt, state) => {
                this.props.entityHoverStateLeav();
            }
        });

        graph.getLabel = function(cell) {
            return cell.contentDiv;
        }
        graph.isHtmlLabel = function(cell) {
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
            // create mxCells for all renderable entities
            for(let entity of this.props.renderableEntities) {
                // create div 
                const placeholderDiv = document.getElementById("mxReactPlaceholder");
                let placeholderDivChild = document.createElement("div");

                placeholderDivChild.style.setProperty('display', '');
                placeholderDiv.appendChild(placeholderDivChild);

                ReactDOM.render(
                    <entity.metadata.mxGraphMetadata.component emfEntity={entity} globalConfig={{}} graphConfig={{}}>
                    </entity.metadata.mxGraphMetadata.component>, 
                    placeholderDivChild);

                var vertex = graph.insertVertex(parent, null, '', 0, 0, entity.metadata.mxGraphMetadata.width + 6, entity.metadata.mxGraphMetadata.height + 6, 'TODO');
                vertex.contentDiv = placeholderDivChild;
                vertex.emfReference = entity;
            }

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
                    if(!cell.emfReference || !association.target)
                        return false;
                    if(cell.emfReference.id === association.source.id)
                        return true;
                    else    
                        return false;
                });
                let targetVertex = allCells.find((cell) => {
                    if(!cell.emfReference || !association.target)
                        return false;
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
        }
        finally
        {
            // Updates the display
            graph.getModel().endUpdate();
        }
    }

    initGraphStyle(graph) {
        // vertex
        var style = {};
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
        style[mxConstants.STYLE_HORIZONTAL_ALIGN] = mxConstants.ALIGN_LEFT;
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
        // graph.getStylesheet().putDefaultVertexStyle(style);
        // cell
        var style = {};
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
        // style[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
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
    entityAssociations: PropTypes.array.isRequired,
    entityHoverStateEnter: PropTypes.func.isRequired,
    entityHoverStateLeav: PropTypes.func.isRequired
};

const mapStateToProps = state => {
    return {
        renderableEntities: state.emfModel.renderable,
        entityAssociations: state.emfModel.associations
    }
};

const mapDispatchToProps = dispatch => {
    return {
        entityHoverStateEnter: (entity) =>  dispatch(entityHoverStateEnter(entity)),
        entityHoverStateLeav: () => dispatch(entityHoverStateLeav()),
        entitySelect: (entity) => dispatch(entitySelect(entity)),
        entityDeselect: () => dispatch(entityDeselect()),
        openContextMenu: (context, x, y) => dispatch(openContextMenu(context, x, y))
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(GraphicalEditor);
