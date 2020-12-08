//components
import {mxConstants, mxPerimeter, mxUtils} from "mxgraph-js"
//images
import datasetIcon from "./../../../images/graph/instances/dataset.svg"
import defaultProcessIcon from "./../../../images/graph/instances/mask.svg"
//mvp_icons
import loadDatasetIcon from "./../../../images/graph/instances_mvp/flow-load-dataset-empty.svg"
import SampleIcon from "./../../../images/graph/instances_mvp/flow-instances-empty.svg"
import keepAttributesIcon from "./../../../images/graph/instances_mvp/flow-feature-selection-empty.svg"
import mlModelIcon from "./../../../images/graph/instances_mvp/flow-model-selected-copy.svg"
import trainIcon from "./../../../images/graph/instances_mvp/flow-train-selected-copy.svg"
//configs
import mxGraphConfig from "./mxGraphConfig"

function configureStylesheet(graph)
{
    var defaultStyle = new Object ();
    defaultStyle[mxConstants.STYLE_LABEL_POSITION] = mxConstants.ALIGN_RIGHT;
    defaultStyle[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
    defaultStyle[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
    graph.getStylesheet().putDefaultVertexStyle(defaultStyle);

    var datasetStyle = new Object();
    datasetStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    datasetStyle[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
    datasetStyle[mxConstants.STYLE_IMAGE] = datasetIcon;
    datasetStyle[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
    graph.getStylesheet().putCellStyle(mxGraphConfig.constants.ASSIGNMENT, datasetStyle);
    
    var processCallStyle = new Object();
    processCallStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    processCallStyle[mxConstants.STYLE_PERIMETER] = mxPerimeter.EllipsePerimeter;
    processCallStyle[mxConstants.STYLE_IMAGE] = defaultProcessIcon;
    processCallStyle[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
    graph.getStylesheet().putCellStyle(mxGraphConfig.constants.PROCESSCALL, processCallStyle);

    //TO-DO: Repeat for split, feature selection, instances selection
    /*Template:
    var Style = new Object();
    Style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    Style[mxConstants.STYLE_PERIMETER] = mxPerimeter.EllipsePerimeter;
    Style[mxConstants.STYLE_IMAGE] = Icon;
    Style[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
    graph.getStylesheet().putCellStyle(mxGraphConfig.constants., Style);
    */
    //loadDataset
    var loadDatasetStyle = new Object();
    loadDatasetStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    loadDatasetStyle[mxConstants.STYLE_PERIMETER] = mxPerimeter.EllipsePerimeter;
    loadDatasetStyle[mxConstants.STYLE_IMAGE] = loadDatasetIcon;
    loadDatasetStyle[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
    graph.getStylesheet().putCellStyle(mxGraphConfig.constants.LOAD_DATASET, loadDatasetStyle);

    //sample
    var SampleStyle = new Object();
    SampleStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    SampleStyle[mxConstants.STYLE_PERIMETER] = mxPerimeter.EllipsePerimeter;
    SampleStyle[mxConstants.STYLE_IMAGE] = SampleIcon;
    SampleStyle[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
    graph.getStylesheet().putCellStyle(mxGraphConfig.constants.SAMPLE, SampleStyle);

    //keepAttributes
    var keepAttributesStyle = new Object();
    keepAttributesStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    keepAttributesStyle[mxConstants.STYLE_PERIMETER] = mxPerimeter.EllipsePerimeter;
    keepAttributesStyle[mxConstants.STYLE_IMAGE] = keepAttributesIcon;
    keepAttributesStyle[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
    graph.getStylesheet().putCellStyle(mxGraphConfig.constants.KEEP_ATTRIBUTES, keepAttributesStyle);

    //mlModelSelection
    var mlModelStyle = new Object();
    mlModelStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    mlModelStyle[mxConstants.STYLE_PERIMETER] = mxPerimeter.EllipsePerimeter;
    mlModelStyle[mxConstants.STYLE_IMAGE] = mlModelIcon;
    mlModelStyle[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
    graph.getStylesheet().putCellStyle(mxGraphConfig.constants.ML_MODEL, mlModelStyle);

    //train
    var trainStyle = new Object();
    trainStyle[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    trainStyle[mxConstants.STYLE_PERIMETER] = mxPerimeter.EllipsePerimeter;
    trainStyle[mxConstants.STYLE_IMAGE] = trainIcon;
    trainStyle[mxConstants.STYLE_FONTCOLOR] = '#b9c0c7';
    graph.getStylesheet().putCellStyle(mxGraphConfig.constants.TRAIN, trainStyle);

    //association
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