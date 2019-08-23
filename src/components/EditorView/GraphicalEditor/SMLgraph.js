//node_modules
import { mxEvent, mxGraph, mxConnectionHandler, mxImage, mxHierarchicalLayout, } from "mxgraph-js";

//helper
import EmfModelHelper from "../../../helper/EmfModelHelper";
import MxGraphConfig from "./mxGraphConfig";

//services
import XtextServices from "../../../serverConnection/XtextServices";
import GraphServices from './mxGraphModelServices';

//images
import connectImage from "../../../images/arrow.png"

class SMLGraph extends mxGraph {

    parent = this.getDefaultParent();
    config = {}
    EMFmodel = {}
    layout = {}

    /**
     * 
     * @param {mxConstant} direction :  mxConstants.DIRECTION_NORTH or mxConstants.DIRECTION_WEST
     */
    initView(direction) {
        this.config = new MxGraphConfig();
        this.layout = new mxHierarchicalLayout(this, direction);
        mxConnectionHandler.prototype.connectImage = new mxImage(connectImage, 10, 10);
        mxConnectionHandler.prototype.moveIconFront = true;
        this.layout.intraCellSpacing = 20;
        this.htmlLabels = true;
        this.setConnectable(true);
        this.labelDisplayOverride();
    }

    /**
     * 
     */
    clear() {
        this.removeCells(this.getChildCells(this.parent, true, true));
    }

    /**
     * 
     */
    render() {
        this.getModel().beginUpdate();
        try {
            var cells = this.addEntities(this.EMFmodel);
            cells.map(cell => this.connectToParent(cell));
            this.connectReferences(this.EMFmodel);
            this.layout.execute(this.parent);
        }
        finally {
            this.getModel().endUpdate();
        }
    }

    /**
     * 
     * @param {JSON object} flatModel EMFModel from DSL after flattening
     */
    updateEMFModel(flatModel) {
        this.EMFmodel = flatModel;
    }

    /**
     * 
     * @param {JSON Object} cellValue: Value from EMFModel
     * @param {string} cellStyle: style from configs
     */
    addEntity(cellValue, cellStyle) {
        return this.insertVertex(this.parent, null, cellValue, 20, 20, 50, 50, cellStyle);
    }

    /**
     * 
     * @param {JSON Object} model 
     */
    addEntities(model) {
        var cells = [];
        model.map(entity => {
            var encodedEntityValue = GraphServices.encode(entity);
            entity['visible'] = this.config.isVisibleEntity(entity);
            var entityStyle = this.config.getStyle(entity.data.className);
            if (entity['visible'] === true) {
                entity['cellObject'] = this.addEntity(encodedEntityValue, entityStyle);
                entity['cellObject'].setValue(entity);
                cells.push(entity['cellObject']);
            }
        })
        return cells;
    }

    /**
     * 
     * @param {mxCell} sourceCell
     * @param {mxCell} targetCell
     */
    addAssociation(sourceCell, targetCell) {
        return this.insertEdge(this.parent, null, null, sourceCell, targetCell);
    }

    /**
     * 
     * @param {JSON Object} model: EMFModel 
     */
    connectReferences(model) {
        model.map(entity => {
            if (entity.data['$ref']) {
                let target = GraphServices.findVisibleTargetCellInModel(entity);
                let decodedReference = GraphServices.decodeReference(entity.data);
                let source = GraphServices.findVisibleSourceCellInModel(decodedReference, model);
                this.addAssociation(source, target);
            }
        })
    };

    /**
     * draws a connection between an mxCell and the next visible ancestor
     * @param {mxCell} cell with EMFEntity in cell.value
     * @returns void 
     */
    connectToParent(cell) {
        var parentCell = GraphServices.findVisibleParent(cell.value);
        if (parentCell !== undefined) {
            this.addAssociation(cell, parentCell);
        }
    }

    /**
     * 
     */
    labelDisplayOverride() {
        this.convertValueToString = (cell) => {
            if (cell.isVertex()) {
                return this.config.getLabelName(cell);
            }
        }
    }

    addDeleteOnDoubleClickListener() {
        this.addListener(mxEvent.DOUBLE_CLICK, function (sender, evt) {
            var cell = evt.getProperty('cell');
            if (cell !== undefined) {
                if (cell.vertex === true) {
                    var entity = cell.value;
                    var enityPath = EmfModelHelper.getFullHierarchy2(entity);
                    XtextServices.deleteEntity(enityPath);
                }
                else {
                    var sourceEntity = cell.source.value;
                    var targetEntity = cell.target.value;
                    var from = EmfModelHelper.getFullHierarchy2(sourceEntity);
                    var to = EmfModelHelper.getFullHierarchy2(targetEntity);
                    XtextServices.deleteAssociation(from, to);
                }
            }
        });
    }


    addCreateAssociationListener() {
        this.connectionHandler.addListener(mxEvent.CONNECT, function (sender, evt, graph) {
            /*reacts to adding of a new edge in existing view, validate via xtext and re-render the view */
            var edge = evt.getProperty('cell');
            var sourceEntity = edge.source.value;
            var targetEntity = edge.target.value;
            var from = EmfModelHelper.getFullHierarchy2(sourceEntity);
            var to = EmfModelHelper.getFullHierarchy2(targetEntity);
            XtextServices.createAssociation(from, to);
        })
    }


    addGraphListeners() {
        this.addCreateAssociationListener();
        this.addDeleteOnDoubleClickListener();
    };


}
export default SMLGraph