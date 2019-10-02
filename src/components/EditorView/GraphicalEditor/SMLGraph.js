//node_modules
import { mxUtils, mxEvent, mxGraph, mxConnectionHandler, mxImage,  mxHierarchicalLayout,} from "mxgraph-js";
//helper
import EmfModelHelper from "../../../helper/EmfModelHelper";
import connectImage from "./../../../images/graph/association-arrow.png"
//services
import XtextServices from "../../../serverConnection/XtextServices";
import MxGraphConfig from "./mxGraphConfig";
import GraphServices from './mxGraphModelServices';

import reduxStore from './../../../reduxStore';
import { openToolbar } from './../../../reducers/toolbar';

class SMLGraph extends mxGraph {

    parent = this.getDefaultParent();
    config = {};
    EMFmodel = undefined;
    layout = {};

    constructor(parentContainer) {
        super(parentContainer);
        if(this.cellEditor) {
            this.cellEditor.destroy();
            this.cellEditor = null;
        }
    }

    /**
     *
     * @param {mxConstant} direction :  mxConstants.DIRECTION_NORTH or mxConstants.DIRECTION_WEST
     */
    initView(direction){
        this.config = new MxGraphConfig();
        this.layout = new mxHierarchicalLayout(this, direction);
        mxConnectionHandler.prototype.connectImage = new mxImage(connectImage,10,10);
        mxConnectionHandler.prototype.moveIconFront=true;
        this.layout.intraCellSpacing = 20;
        this.htmlLabels = true;
        this.setConnectable(true);
        this.labelDisplayOverride();
    }

    /**
     * erases everything on graph.view
     */
    clear(){
        this.removeCells(this.getChildCells(this.parent, true, true));
        console.log(this.model)
    }

    /**
     * renders the model stored in this.EMFModel
     *
     */
    render(){ 
        if(this.EMFmodel === undefined)
            return;
        this.getModel().beginUpdate();
        try{
            var cells = this.addEntities(this.EMFmodel);
            cells.map(cell => {
                this.connectToParent(cell);
            });
            this.connectReferences(this.EMFmodel);
            var nodes = this.returnAllVertices();
            nodes.forEach(cell => {
                if (!this.isSourceNode(cell)){
                    this.addPlusVertex(cell);
                }
            })
            this.layout.execute(this.parent);
        }
        finally {
            this.getModel().endUpdate();
        }
    }

    /**
     *
     * @param {JSON} flatModel EMFModel from DSL after flattening
     */
    updateEMFModel(flatModel){
        this.EMFmodel=flatModel;
    }

    /**
     *
     * @param {JSON} cellValue: Value from EMFModel
     * @param {string} cellStyle: style from configs
     */
    addEntity(cellValue, cellStyle){
        return this.insertVertex(this.parent, null, cellValue, 20, 20, 50, 50, cellStyle);
    }

    /**
     * draws all entities stored in this.EMFModel
     *
     *
     * @param {*} model
     */
    addEntities(model){
        if( model === undefined )
            return [];
        var cells = [];
        model.forEach(entity=>{
            var encodedEntityValue = GraphServices.encode(entity);
            entity['visible'] = this.config.isVisibleEntity(entity);
            var entityStyle = this.config.getStyle(entity.data.className);
            if (entity['visible'] === true) {
                entity['cellObject'] = this.addEntity(encodedEntityValue, entityStyle);
                entity['cellObject'].setValue(entity);
                cells.push(entity['cellObject']);
            }
        });
        return cells;
    }

    /**
     *
     * @param {mxCell} sourceCell
     * @param {mxCell} targetCell
     */
    addAssociation(sourceCell, targetCell){
        return this.insertEdge(this.parent, null, null, sourceCell, targetCell);
    }

    /**
     *
     * @param {JSON} model: EMFModel
     *  draws all '$ref' associations from this.EMFModel
     */
    connectReferences(model) {
        model.forEach(entity => {
            if (entity.data['$ref']){
                let target = GraphServices.findVisibleTargetCellInModel(entity);
                let decodedReference = GraphServices.decodeReference(entity.data);
                let source = GraphServices.findVisibleSourceCellInModel(decodedReference, this.EMFmodel);
                this.addAssociation(source, target);
            }
        })
    };

    addPlusVertex(cell){
        if(cell.value !== "plusButton"){
            var plus = this.addEntity("plusButton", "fillColor=orange");
            plus.setParent(cell);
            this.addAssociation(cell, plus);
        }
    }

    returnAllEdges(){
        var edges = []
        for (var index in this.model.cells){
            if (this.model.cells[index].edge === true){
                edges.push(this.model.cells[index])
            }
        }
        return edges
    }
    returnAllVertices(){
        var vertices = []
        for (var index in this.model.cells){
            if (this.model.cells[index].vertex === true){
                vertices.push(this.model.cells[index])
            }
        }
        return vertices
    }

    returnAllSourceNodes(){
        var sourceNodes = [];
        var edges = this.returnAllEdges();
        edges.forEach(edge=>{
            sourceNodes.push(edge.source)
        })
        return sourceNodes;
    }

    isSourceNode(cell){
        var result = false;
        var sourceNodes = this.returnAllSourceNodes();
        var comparedSources=this.model.filterCells(sourceNodes, function(source){
            return source.id === cell.id
        })
        if (comparedSources.length !== 0){
            result = true;
        }
        return result;
    }

    /**
     * draws a connection between an mxCell and the next visible ancestor
     * @param {mxCell} cell with EMFEntity in cell.value
     * @returns void
     */
    connectToParent(cell){
        var parentCell = GraphServices.findVisibleParent(cell.value);
        if (parentCell !== undefined){
            this.addAssociation(cell, parentCell);
        }
    }


    /**
     *
     * @param {JSON} entityValue: EMFEntity
     * @returns {mxCell} with label "object" and attributes from EntityValue
     */
    encodeToMxCell(entityValue){
        const xmlDoc = mxUtils.createXmlDocument();
        var newMxCell = xmlDoc.createElement("object");
        for (let prop in entityValue) {
            newMxCell.setAttribute(prop, entityValue[prop]);
        }
        return newMxCell;
    }

    /**
     *
     */
    labelDisplayOverride(){
        this.convertValueToString=(cell) =>{
            if (cell.isVertex()&& cell.value!== "plusButton") {
                return this.config.getLabelName(cell);
            }
        }
    }


    addDeleteOnDoubleClickListener(){
        this.addListener(mxEvent.DOUBLE_CLICK, function(sender, evt){
            let cell = evt.getProperty('cell');
            reduxStore.dispatch(openToolbar(cell, evt.properties.event.pageX, evt.properties.event.pageY))
        });
    }


    addCreateAssociationListener(){
        this.connectionHandler.addListener(mxEvent.CONNECT, function (sender,evt, graph){
            /*reacts to adding of a new edge in existing view, validate via xtext and re-render the view */
            var edge = evt.getProperty('cell');
            var sourceEntity=edge.source.value;
            var targetEntity=edge.target.value;
            var from=EmfModelHelper.getFullHierarchy2(sourceEntity);
            var to=EmfModelHelper.getFullHierarchy2(targetEntity);
            XtextServices.createAssociation(from,to);
        })
    }


    addGraphListeners(){
        this.addCreateAssociationListener();
        this.addDeleteOnDoubleClickListener();
    };


}
export default SMLGraph
