//node_modules
import { mxUtils } from "mxgraph-js";
//helper
import EmfModelHelper from "../../../helper/EmfModelHelper";


export default class MxGraphModelServices {
    renderFullText(fullText, parent, graph, config) {
        let flatModel = EmfModelHelper.flattenEmfModelTree(JSON.parse(fullText));
        //adding all visible nodes at once
        var vertices = this.addAllNodes(flatModel, parent, graph, config);
        //adding parent-child edges
        var edges = [];
        for (let i in vertices){
           var parentCell = this.findVisibleParent(vertices[i].value);
           var childCell = vertices[i].value['cellObject'];
           if(parentCell !== undefined){
                 edges.push(graph.insertEdge(graph.getDefaultParent(), null, null,parentCell, childCell));
            }
        }
        //adding $ref edges
        edges = edges.concat(this.connectReferences(flatModel,graph));
    }

    /*adds all nodes, returns an array with all added nodes*/
    addAllNodes(flatModel, parent, graph, config) {
        var vertices = {};
        for (var i in flatModel) {
            var encodedEntity = this.encode(flatModel[i]);
            flatModel[i]['visible'] = this.isVisible(flatModel[i]);
            var entityStyle = config.getConfig(flatModel[i].data.className, "style", config.configs);
            if(flatModel[i]['visible'] === true){
                var encodedFunctionNode = graph.insertVertex(parent, null, encodedEntity, 20, 20, 50, 50, entityStyle);
                flatModel[i]['cellObject'] = encodedFunctionNode;
                encodedFunctionNode.setValue(flatModel[i]);
                vertices[i] = encodedFunctionNode;
            }     
        }
        return vertices;
    }
    //finds an mxcell of the first visible ancestor
    //input: EmfEntity from flattenModel
    findVisibleParent(entity) {
        if(entity['parent'] !== undefined){
            var potentialParent = entity['parent'];
            while (potentialParent['parent'] !== undefined && potentialParent['visible'] === false){
                potentialParent = potentialParent['parent'];
                } 
            var parentCell = potentialParent['cellObject'];
            return parentCell;
              
        }
    }

    //TO_DO : bring it into configs
    isVisible(entity) {
        if(entity.self !== "ref" && entity.data.className !== "org.xtext.example.mydsl.myDsl.Workflow" && entity.data.className !== "org.xtext.example.mydsl.myDsl.Reference" && entity.data.className !== "org.xtext.example.mydsl.myDsl.UnconnectedExpressionStatement" && entity.data.className !== "org.xtext.example.mydsl.myDsl.ArrayLiteral" && entity.data.className !== "org.xtext.example.mydsl.myDsl.TimeLiteral"){
            return true;
        }
        else return false;
    }

    connectReferences(flatModel, graph) {
        var refEdges = [];
        for (let i in flatModel){
            if (flatModel[i].data['$ref']){
                //find visible target cell 
                var potentialTarget = flatModel[i];
                var target;
                if (potentialTarget.visible !== true){
                    target = this.findVisibleParent(potentialTarget);
                }
                else{
                    target = potentialTarget['cellObject'];
                }
                //decode the $ref
                let ref = flatModel[i].data['$ref'];
                ref = ref.replace("//@", "");
                var refArrayAndIndex = ref.split(".");
                let name = refArrayAndIndex[0]+"["+refArrayAndIndex[1]+"]";
                //find visible source cell
                let potentialSource = flatModel.filter(entity => entity["self"] === name)[0];
                var source;
                if(potentialSource.visible !== true){
                    source = this.findVisibleParent(source);
                }
                else{
                    source = potentialSource['cellObject'];
                }
                //connect target and source and add them to array of edges
                refEdges.push(graph.insertEdge(graph.getDefaultParent(), null, null,target, source));
            }
        }
        return refEdges;

    }

    //value:JSON object
    encode(value) {
        /* gets a JSON and returns a new mxCell object with JSON information saved as "attribute" */
        const xmlDoc = mxUtils.createXmlDocument();
        var newObject2 = xmlDoc.createElement("object");
        for (let prop in value) {
            newObject2.setAttribute(prop, value[prop]);
        }
        return newObject2;
    }
    labelDisplayOveride(graph) { /* Overrides method to provide a cell label in the display */

        //TODO: Put it in configs
        graph.convertValueToString = (cell) => {
            if (cell.isVertex()) {
                if (cell.value.data.ref) {
                    const name = "PROCESS: " + "\n" + cell.value.data.ref;
                    return name;
                }
                if (cell.value.data.value) {
                    const name = "CONFIG: " + "\n" + cell.value.data.value;
                    return name;
                }
                if (cell.value.data.name) {
                    const name = "DATASET: " + "\n" + cell.value.data.name;
                    return name;
                }
                if (cell.value.data.className === "org.xtext.example.mydsl.myDsl.DateLiteral"){
                    const name = "CONFIG: " + "\n year: " + cell.value.data.year + ", month: " + cell.value.data.month + ", day: " +cell.value.data.day 
                    return name;
                }
                if (cell.value.self === "seconds"){
                    const name = "CONFIG: " + "\n hours: " + cell.value.parent.data.hours + ", minutes: " + cell.value.parent.data.minutes + ", seconds: " + cell.value.data.seconds; 
                    return name;
                }
            }
            return '';
        };
    }
}
