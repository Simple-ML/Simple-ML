import {mxUtils} from "mxgraph-js"


export default class MxGraphModelServices {
renderFullText(fullText, parent, graph, config){
var workflow= JSON.parse(fullText);
var vertices=[];
if (workflow.entities){
vertices=this.addAllNodes(workflow.entities, parent, graph, config);
this.addAllEdges(workflow.entities, vertices, parent, graph);
}
}

/*adds all nodes, returns an array with all added nodes*/
addAllNodes(entities, parent, graph, config){
var vertices={};

for (var i in entities){
var encodedEntity=this.encode(entities[i]);
var entityStyle=config.getConfig(entities[i].className, "style", config.configs);
var encodedFunctionNode=graph.insertVertex(parent, null, encodedEntity, 20, 20, 50, 50, entityStyle);
vertices[i]=encodedFunctionNode;
}
return vertices;
}

/*adds all edges of all already created nodes (vertices) of specific class (entities) to the viewer
*returns an array with another allNodeEdges arrays(see addNodeEdges) for each node sorted by id */
addAllEdges(entities, vertices, parent, graph){
var allModelEdges={};
for(var i in entities){
allModelEdges[i]=this.addNodeEdges(entities[i], vertices[i], vertices, parent, graph);
}
return allModelEdges;
}

/*adds all edges of one node (vertice) to the viewer
*returns an array of all edges who have this vertice as target */
addNodeEdges(entity, vertice, otherVertices, parent, graph){
var allNodeEdges=[];
for (var key in entity){
if (Array.isArray(entity[key])){
/*looks for new edges in the attribute if the attribute is an array*/
for (var j in entity[key]){
let refIndex=this.findReference(entity[key][j]);
if (refIndex){
allNodeEdges.push(graph.insertEdge(parent, null, entity["name"], vertice, otherVertices[refIndex[1]], "strokeColor=lightgreen"));
}
}
}
else{
/*looks for new edges in the attribute if the attribute is not array*/
let refIndex=this.findReference(entity[key]);
if (refIndex){
allNodeEdges.push(graph.insertEdge(parent, null, entity["name"], vertice, otherVertices[refIndex[1]], "strokeColor=blue"));
}
}
}
return allNodeEdges;
}

findReference(key){
/*help function to find if there is a reference in the key,
*returns an array temp with temp[0]: class whih the reference refers to, and temp [1]: the index of that specific object in this class,
*otherwise if no reference found returns null*/
var ref = key["$ref"]
if (ref){
ref = ref.replace("//@", "")
var temp = ref.split(".")
return temp;
}
else{
return null
}
}

//value:JSON object
encode(value) {
/* gets a JSON and returns a new mxCell object with JSON information saved as "attribute" */
const xmlDoc = mxUtils.createXmlDocument();
var newObject2 = xmlDoc.createElement("object");
for(let prop in value) {
//newObject2.setAttribute(prop, JSON.stringify(value[prop]));
newObject2.setAttribute(prop, value[prop]);
}
return newObject2;
}
labelDisplayOveride(graph) { /* Overrides method to provide a cell label in the display */
graph.convertValueToString = (cell)=> {
if (mxUtils.isNode(cell.value)) {
if (cell.value.nodeName.toLowerCase() === 'function'||cell.value.nodeName.toLowerCase() === 'datasource'||cell.value.nodeName.toLowerCase() === 'method'||cell.value.nodeName.toLowerCase() === 'object') {
const name = cell.getAttribute('name', '');
return name;
}
}
return '';
};
}
}