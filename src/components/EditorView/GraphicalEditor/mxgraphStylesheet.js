import {mxConstants, mxPerimeter, mxUtils} from "mxgraph-js"
import datasetIcon from "./../../../images/graph/instances/dataset.svg"
import defaultProcessIcon from "./../../../images/graph/instances/mask.svg"
import endArrowIcon from "./../../../images/graph/oval.svg"
import mxGraphConfig from "./mxGraphConfig"

function configureStylesheet(graph)
{
    var datasetStyle = new Object();
    datasetStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    datasetStyle[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
    datasetStyle[mxConstants.STYLE_IMAGE] = datasetIcon;
    datasetStyle[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
    graph.getStylesheet().putCellStyle(mxGraphConfig.constants.DATASET, datasetStyle);
    
    var processCallStyle = new Object();
    datasetStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    processCallStyle[mxConstants.STYLE_IMAGE] = defaultProcessIcon;
    processCallStyle[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
    graph.getStylesheet().putCellStyle(mxGraphConfig.constants.PROCESSCALL, processCallStyle);

    var edgeStyle = new Object();
    edgeStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
    edgeStyle[mxConstants.STYLE_ENDARROW] = mxConstants.SHAPE_IMAGE;
    edgeStyle[mxConstants.STYLE_IMAGE] = datasetIcon;
    edgeStyle[mxConstants.STYLE_STROKEWIDTH] = "2px";
    edgeStyle[mxConstants.STYLE_STROKECOLOR] = "#d8d8d8";
    edgeStyle[mxConstants.STYLE_IMAGE_WIDTH] = '48';
    edgeStyle[mxConstants.STYLE_IMAGE_HEIGHT] = '48';
    graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);
    /**
     * align: "center"
endArrow: "classic"
fontColor: "#446299"
shape: "connector"
strokeColor: "#6482B9"
verticalAlign: "middle"
     */
    //graph.getStylesheet().putDefaultEdgeStyle(edgeStyle)
//////////////////////////
};
export default configureStylesheet;