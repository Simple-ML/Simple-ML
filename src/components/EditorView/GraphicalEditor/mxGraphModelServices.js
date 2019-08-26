//node_modules
import { mxUtils } from "mxgraph-js";

class GraphServices {

    /**
     * 
     * @param {JSON} value 
     * @returns {mxCell} new mxCell with given value 
     */
    static encode(value) {
        /* gets a JSON and returns a new mxCell object with JSON information saved as "attribute" */
        const xmlDoc = mxUtils.createXmlDocument();
        var newObject = xmlDoc.createElement("object");
        for (let prop in value) {
            newObject.setAttribute(prop, value[prop]);
        }
        return newObject;
    }

    /**
     * 
     * @param {JSON Object} data with attribute $ref
     * @returns {String} name of object in $ref
     */
    static decodeReference(data) {
        console.log(data)
        let ref = data['$ref']
        ref = ref.replace("//@", "");
        var refArrayAndIndex = ref.split(".");
        let name = refArrayAndIndex[0] + "[" + refArrayAndIndex[1] + "]";
        return name;
    };

    /**
     * 
     * @param {object} entity: EMFEntity from flatten EMF Model () 
     * @returns {mxCell} mxcell of the first visible ancestor
     */
    static findVisibleParent(entity) {
        if (entity['parent'] !== undefined) {
            var potentialParent = entity['parent'];
            while (potentialParent['parent'] !== undefined && potentialParent['visible'] === false) {
                potentialParent = potentialParent['parent'];
            }
            var parentCell = potentialParent['cellObject'];
            return parentCell;
        }
    };

    /**
     * 
     * @param {string} reference decoded Reference
     * @param {JSON} model flattened model
     * @returns {mxCell} source cell
     */
    static findVisibleSourceCellInModel(reference, model) {
        let potentialSource = model.filter(entity => entity["self"] === reference)[0];
        var source;
        if (potentialSource.visible !== true) {
            source = this.findVisibleParent(source);
        }
        else {
            source = potentialSource['cellObject'];
        }
        return source;
    }

    /**
     * 
     * @param {JSON Object} potentialTarget data with attribute 'isVisible'
     * @returns next visible ancestor mxCell
     */
    static findVisibleTargetCellInModel(potentialTarget) {
        console.log(potentialTarget)
        var target;
        if (potentialTarget.visible !== true) {
            target = this.findVisibleParent(potentialTarget);
        }
        else {
            target = potentialTarget['cellObject'];
        }
        return target;
    }
}

export default {
    encode: (value) => GraphServices.encode(value),
    decodeReference: (data) => GraphServices.decodeReference(data),
    findVisibleParent: (entity) => GraphServices.findVisibleParent(entity),
    findVisibleSourceCellInModel: (reference, model) => GraphServices.findVisibleSourceCellInModel(reference, model),
    findVisibleTargetCellInModel: (potentialTarget) => GraphServices.findVisibleTargetCellInModel(potentialTarget),
}




