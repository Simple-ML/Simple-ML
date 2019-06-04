import React from 'react';
import ReactDOM from 'react-dom'
import "./JsonComponent.css"
import XtextServices from "./ServerConnection/xtextServices";
import {mxClient, mxGraph, mxUtils, mxCodec, mxGraphModel} from "mxgraph-js"
class JsonComponent extends React.Component{
    constructor() {
        super()
        var container = ReactDOM.findDOMNode(this.refs.mxgraphDiv);
        //const graph = new mxGraph(container);
        const graph = new mxGraph();
        var container2 = ReactDOM.findDOMNode(this.refs.mxgraphDiv2);
        //const graph2 = new mxGraph(container2);
        const graph2 = new mxGraph();
        this.state={
            graph:graph,
            graph2:graph2
        }
        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                const {graph}=this.state;
                let parent=graph.getDefaultParent();
                let x=Math.random()*320;
                let y=Math.random()*240;
                graph.insertVertex(parent, null, 'JSON', x, y, 40, 20);
                console.log("constructor success listener")
                this.setState({graph:graph});
            }
        });
    }

    componentDidMount=()=>{
        console.log("component did mount")
        //var container = ReactDOM.findDOMNode(this.refs.mxgraphDiv);

        if (!mxClient.isBrowserSupported()) {
            // Displays an error message if the browser is not supported.
            mxUtils.error("Browser is not supported!", 200, false);
          } else {
            //const graph_clone=Object.assign(this.state.graph);
            const {graph} = this.state;
            //const graph = new mxGraph(container);
            var parent = graph.getDefaultParent();
            graph.getModel().beginUpdate();
             try{
                console.log(graph.getModel().updateLevel)
                var v1 = graph.insertVertex(parent, null, 'Hello,', 20, 20, 80, 30);
                var v2 = graph.insertVertex(parent, null, 'World!', 200, 150, 80, 30);
                graph.insertEdge(parent, null, '', v1, v2); 
             }
             finally{
                graph.getModel().endUpdate();
                console.log(graph.getModel())
             }
             //get XML
             var encoder = new mxCodec();
             var graph2_model_result=encoder.encode(graph.getModel());
             console.log(graph2_model_result)
             var graph2_xml=mxUtils.getXml(graph2_model_result);
             console.log(graph2_xml)

             //merge xml into graph
             var cells = []
             var dx = (dx != null) ? dx : 0;
             var dy = (dy != null) ? dy : 0;
             var doc = mxUtils.parseXml(graph2_xml);
             var node = doc.documentElement;
             var graph2= new mxGraph(container2);
             if (node != null)
						{
							var model = new mxGraphModel();
							var codec = new mxCodec(node.ownerDocument);
							codec.decode(node, model);
							
							var childCount = model.getChildCount(model.getRoot());
							var targetChildCount = graph2.model.getChildCount(graph2.model.getRoot());
							
							// Merges existing layers and adds new layers
							graph2.model.beginUpdate();
							try
							{
								for (var i = 0; i < childCount; i++)
								{
									var parent = model.getChildAt(model.getRoot(), i);
									
									// Adds cells to existing layers if not locked
									if (targetChildCount > i)
									{
										// Inserts into active layer if only one layer is being pasted
										var target = (childCount == 1) ? graph2.getDefaultParent() : graph2.model.getChildAt(graph2.model.getRoot(), i);
										
										if (!graph2.isCellLocked(target))
										{								
											var children = model.getChildren(parent);
                                            cells = cells.concat(graph2.importCells(children, dx, dy, target));
                                            console.log(cells)
										}
									}
									else
									{
										// Delta is non cascading, needs separate move for layers
										parent = graph2.importCells([parent], 0, 0, graph2.model.getRoot())[0];
										var children = graph2.model.getChildren(parent);
										graph2.moveCells(children, dx, dy);
                                        cells = cells.concat(children);
                                        console.log(cells)
									}
								}
							}
							finally
							{
                                graph2.model.endUpdate();
                                console.log(graph2.getModel())
							}
						}



            // let modelstring =mxUtils.toString(graph.getModel());
             //JSON.parse(modelstring);
             var container2 = ReactDOM.findDOMNode(this.refs.mxgraphDiv2);
             /*var encoder = new mxCodec();
             var graph2_model_result=encoder.encode(graph.getModel());
             console.log(graph2_model_result)
             var graph2_xml=mxUtils.getXml(graph2_model_result);
             console.log(graph2_xml)
             var doc= mxUtils.parseXml(graph2_xml);
             console.log(doc)
             var decoder= new mxCodec(doc);*/
             //var new_model = mxUtils.clone(graph.getModel())
             //var graph2_model=decoder.decode(doc.documentElement);
             //console.log(graph2_model)
             //const graph2 = new mxGraph(container2);
             //const graph2 = new mxGraph(container2, graph2_model);
             
             /*var elt = doc.documentElement.firstChild;
             var cells= [];

             while(elt!=null){
                 cells.push(decoder.decode(elt));
                 elt =elt.nextSibling;
             }
             console.log(cells)
             const graph2 = new mxGraph(container2);
             //graph2.addCells(cells)*/
             //this.setState({graph}) //CAUTION! calls render()
          }
    }

    render(){
        console.log("render")
        const {graph, graph2}=this.state;
        var container = ReactDOM.findDOMNode(this.refs.mxgraphDiv);
        const graph_new = new mxGraph(container, graph.getModel());
        graph_new.getModel().beginUpdate();
        try{ 
            console.log("graph_new")
            console.log(graph_new.getModel())
        }
        finally{
            graph_new.getModel().endUpdate();
        }



       // graph2.getModel().clear(); //empty graph2
       // graph2.addCells(graph.cloneCells(graph.getChildCells(graph.getDefaultParent()))); //add all cells from graph to graph2
        console.log("graph")
        console.log(graph.getModel());
        console.log("clone")
        console.log(graph2.getModel());

        /*XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                console.log("render success listener");
                graph2.addCells(graph.cloneCells(graph.getChildCells(graph.getDefaultParent())));

                console.log(graph);
                console.log("clone")
                console.log(graph2);
            }
        });*/
        return (<div>
            <div className="container" ref="mxgraphDiv" > </div>
            <div className="container2" ref="mxgraphDiv2" > </div>
        </div>

        );
    }
}
export default JsonComponent;
