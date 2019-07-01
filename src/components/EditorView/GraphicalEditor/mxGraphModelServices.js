//node_modules
import { mxUtils } from "mxgraph-js";
//helper
import EmfModelHelper from "../../../Helper/EmfModelHelper";

export default class MxGraphModelServices {
    renderFullText(fullText, parent, graph, config) {
        let flatModel= (new EmfModelHelper()).flattenEmfModelTree(JSON.parse(fullText));
        console.log(flatModel)
        var vertices = this.addAllNodes(flatModel, parent, graph, config);
        this.addAllEdges2(flatModel, vertices, parent, graph);
    }

    /*adds all nodes, returns an array with all added nodes*/
    addAllNodes(flatModel, parent, graph, config) {
        var vertices = {};
        for (var i in flatModel) {
            var encodedEntity = this.encode(flatModel[i]);
            var entityStyle = config.getConfig(flatModel[i].data.className, "style", config.configs);
            var encodedFunctionNode = graph.insertVertex(parent, null, encodedEntity, 20, 20, 50, 50, entityStyle);
            encodedFunctionNode.setValue(flatModel[i])
            vertices[i] = encodedFunctionNode;
        }
        console.log(vertices);
        return vertices;
    }

    /*adds all edges of all already created nodes (vertices) of specific class (entities) to the viewer
    *returns an array with another allNodeEdges arrays(see addNodeEdges) for each node sorted by id */
    addAllEdges(entities, vertices, parent, graph) {
        var allModelEdges = {};
        for (var i in entities) {
            allModelEdges[i] = this.addNodeEdges(entities[i], vertices[i], vertices, parent, graph);
        }
        return allModelEdges;
    }

    /*adds all edges of one node (vertice) to the viewer
    *returns an array of all edges who have this vertice as target */
    addNodeEdges(entity, vertice, otherVertices, parent, graph) {
        var allNodeEdges = [];
        for (var key in entity) {
            if (Array.isArray(entity[key])) {
                /*looks for new edges in the attribute if the attribute is an array*/
                for (var j in entity[key]) {
                    let refIndex = this.findReference(entity[key][j]);
                    if (refIndex) {
                        allNodeEdges.push(graph.insertEdge(parent, null, entity["name"], vertice, otherVertices[refIndex[1]], "strokeColor=lightgreen"));
                    }
                }
            }
            else {
                /*looks for new edges in the attribute if the attribute is not array*/
                let refIndex = this.findReference(entity[key]);
                if (refIndex) {
                    allNodeEdges.push(graph.insertEdge(parent, null, entity["name"], vertice, otherVertices[refIndex[1]], "strokeColor=blue"));
                }
            }
        }
        return allNodeEdges;
    }
    addNodeEdges2(flatModelEntity, vertice, otherVertices, parent, graph) {
        var allNodeEdges = [];
        console.log(flatModelEntity.parent);
        for (var key in flatModelEntity) {
            if (Array.isArray(flatModelEntity[key])) {
                /*looks for new edges in the attribute if the attribute is an array*/
                for (var j in flatModelEntity[key]) {
                    let refIndex = this.findReference(flatModelEntity[key][j]);
                    if (refIndex) {
                        allNodeEdges.push(graph.insertEdge(parent, null, flatModelEntity["name"], vertice, otherVertices[refIndex[1]], "strokeColor=lightgreen"));
                    }
                }
            }
            else {
                /*looks for new edges in the attribute if the attribute is not array*/
                //let refIndex = this.findReference(flatModelEntity[key]);

                if (flatModelEntity[key]) {
                   // allNodeEdges.push(graph.insertEdge(parent, null, flatModelEntity["name"], vertice, otherVertices[refIndex[1]], "strokeColor=blue"));
                }
            }
        }
        return allNodeEdges;
    }

    addAllEdges2(flatModel, vertices, parent, graph) {
        var allModelEdges = {};
        for (var i in flatModel) {
            allModelEdges[i] = this.addNodeEdges2(flatModel[i], vertices[i], vertices, parent, graph);
        }
        return allModelEdges;
    }

    findReference(key) {
        /*help function to find if there is a reference in the key,
        *returns an array temp with temp[0]: class whih the reference refers to, and temp [1]: the index of that specific object in this class,
        *otherwise if no reference found returns null*/
        var ref = key["$ref"];
        if (ref) {
            ref = ref.replace("//@", "");
            var temp = ref.split(".");
            return temp;
        }
        else {
            return null;
        }
    }

    //value:JSON object
    encode(value) {
        /* gets a JSON and returns a new mxCell object with JSON information saved as "attribute" */
        const xmlDoc = mxUtils.createXmlDocument();
        var newObject2 = xmlDoc.createElement("object");
        for (let prop in value) {
            if(value[prop]){}
            //newObject2.setAttribute(prop, JSON.stringify(value[prop]));
            newObject2.setAttribute(prop, value[prop]);
        }
        return newObject2;
    }
    labelDisplayOveride(graph) { /* Overrides method to provide a cell label in the display */
        graph.convertValueToString = (cell) => {
            if (cell.isVertex()) {
                if (cell.value.data.ref) {
                    const name = "PROCESS: "+"\n"+cell.value.data.ref;
                    return name;
                }
                if (cell.value.data.value) {
                    const name = "CONFIG: "+"\n"+cell.value.data.value;
                    return name;
                }
                if (cell.value.data.name) {
                    const name = "DATASET: "+"\n"+cell.value.data.name;
                    return name;
                }
            }
            return '';
        };
    }
}
