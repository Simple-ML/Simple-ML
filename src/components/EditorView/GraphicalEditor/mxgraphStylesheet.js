import {mxConstants, mxPerimeter, mxUtils} from "mxgraph-js"
import datasetIcon from "./../../../images/graph/instances/dataset.svg"
import defaultProcessIcon from "./../../../images/graph/instances/mask.svg"
import mxGraphConfig from "./mxGraphConfig"

function configureStylesheet(graph)
{
    var datasetStyle = new Object();
    datasetStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    datasetStyle[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
    datasetStyle[mxConstants.STYLE_IMAGE] = datasetIcon;
    datasetStyle[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
    graph.getStylesheet().putCellStyle(mxGraphConfig.constants.ASSIGNMENT, datasetStyle);
    
    var processCallStyle = new Object();
    processCallStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    processCallStyle[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
    processCallStyle[mxConstants.STYLE_IMAGE] = defaultProcessIcon;
    processCallStyle[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
    graph.getStylesheet().putCellStyle(mxGraphConfig.constants.PROCESSCALL, processCallStyle);

    //TO-DO: Repeat for split, feature selection, instances selection

    var edgeStyle = new Object();
    edgeStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
    edgeStyle[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_OVAL;
    edgeStyle[mxConstants.STYLE_ROUNDED] = true;
    edgeStyle[mxConstants.STYLE_STROKEWIDTH] = "2";
    edgeStyle[mxConstants.STYLE_STROKECOLOR] = "#d8d8d8";
    edgeStyle[mxConstants.STYLE_ENDSIZE] = "3";
    edgeStyle[mxConstants.STYLE_TARGET_PERIMETER_SPACING]="5";
    graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);
};
export default configureStylesheet;