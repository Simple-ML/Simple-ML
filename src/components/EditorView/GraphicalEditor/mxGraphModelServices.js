//node_modules
import { mxUtils } from "mxgraph-js";

class GraphServices {
    /**
     * 
     * @param {object} entity: EMFEntity from flatten EMF Model () 
     * @returns {mxCell} mxcell of the first visible ancestor
     */
    static findVisibleParent(entity) {
        if(entity['parent'] !== undefined){
            var potentialParent = entity['parent'];
            while (potentialParent['parent'] !== undefined && potentialParent['visible'] === false){
                potentialParent = potentialParent['parent'];
                }
            var parentCell = potentialParent['cellObject'];
            return parentCell;
        }
    };

    /**
     * 
     * @param {*} data 
     */
    static decodeReference(data){
        let ref = data['$ref']
        ref = ref.replace("//@", "");
        var refArrayAndIndex = ref.split(".");
        let name = refArrayAndIndex[0]+"["+refArrayAndIndex[1]+"]";
        return name;
    };

    /**
     * 
     * @param {*} potentialTarget 
     */
    static findVisibleTargetCellInModel(potentialTarget){
        var target;
        if (potentialTarget.visible !== true){
            target = this.findVisibleParent(potentialTarget);
        }
        else{
            target = potentialTarget['cellObject'];
        }
        return target;
    }
    
    
    /**
     * 
     * @param {string} reference decoded Reference
     * @param {JSON} model flattened model
     * @return {mxCell} source cell
     */
    static findVisibleSourceCellInModel(reference, model){
        let potentialSource = model.filter(entity => entity["self"] === reference)[0];
        var source;
        if(potentialSource.visible !== true){
            source = this.findVisibleParent(source);
        }
        else{
            source = potentialSource['cellObject'];
        }
        return source;
    }
    
    //value:JSON object
    /**
     * 
     * @param {*} value 
     */
    static encode(value) {
        /* gets a JSON and returns a new mxCell object with JSON information saved as "attribute" */
        const xmlDoc = mxUtils.createXmlDocument();
        var newObject2 = xmlDoc.createElement("object");
        for (let prop in value) {
            newObject2.setAttribute(prop, value[prop]);
        }
        return newObject2;
    }
}

export default {
    addAllListeners : (graph) => GraphServices.addAllListeners(graph),
    labelDisplayOveride: (graph) => GraphServices.labelDisplayOveride(graph),
    renderFullText: (graph, model, config) => GraphServices.renderFullText(graph, model, config),
    encode:(value) => GraphServices.encode(value),
    isVisible: (entity) => GraphServices.isVisible(entity),
    findVisibleParent: (entity) => GraphServices.findVisibleParent(entity),
    findVisibleSourceCellInModel: (reference, model) => GraphServices.findVisibleSourceCellInModel(reference, model),
    findVisibleTargetCellInModel: (potentialTarget) => GraphServices.findVisibleTargetCellInModel(potentialTarget),
    decodeReference: (data)=> GraphServices.decodeReference(data)
}





