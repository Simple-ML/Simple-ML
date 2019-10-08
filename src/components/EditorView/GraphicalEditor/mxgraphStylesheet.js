import {mxConstants, mxPerimeter, mxUtils} from "mxgraph-js"
function configureStylesheet(graph)
{
    var style = new Object();
    style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
    style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
    style[mxConstants.STYLE_IMAGE] = 'images/icons48/keys.png';
    style[mxConstants.STYLE_FONTCOLOR] = '#FFFFFF';
    graph.getStylesheet().putCellStyle('image', style);
    
    style = mxUtils.clone(style);
    style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_LABEL;
    style[mxConstants.STYLE_STROKECOLOR] = '#000000';
    style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
    style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
    style[mxConstants.STYLE_IMAGE_ALIGN] = mxConstants.ALIGN_CENTER;
    style[mxConstants.STYLE_IMAGE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
    style[mxConstants.STYLE_IMAGE] = 'images/icons48/gear.png';
    style[mxConstants.STYLE_IMAGE_WIDTH] = '48';
    style[mxConstants.STYLE_IMAGE_HEIGHT] = '48';
    style[mxConstants.STYLE_SPACING_TOP] = '56';
    style[mxConstants.STYLE_SPACING] = '8';
    graph.getStylesheet().putCellStyle('bottom', style);
    
    style = mxUtils.clone(style);
    style[mxConstants.STYLE_IMAGE_VERTICAL_ALIGN] = mxConstants.ALIGN_BOTTOM;
    style[mxConstants.STYLE_IMAGE] = 'images/icons48/server.png';
    delete style[mxConstants.STYLE_SPACING_TOP];
    graph.getStylesheet().putCellStyle('top', style);
    
    style = mxUtils.clone(style);
    style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
    style[mxConstants.STYLE_IMAGE_ALIGN] = mxConstants.ALIGN_LEFT;
    style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
    style[mxConstants.STYLE_IMAGE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
    style[mxConstants.STYLE_IMAGE] = 'images/icons48/earth.png';
    style[mxConstants.STYLE_SPACING_LEFT] = '55';
    style[mxConstants.STYLE_SPACING] = '4';
    graph.getStylesheet().putCellStyle('right', style);
    
    style = mxUtils.clone(style);
    style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_RIGHT;
    style[mxConstants.STYLE_IMAGE_ALIGN] = mxConstants.ALIGN_RIGHT;
    delete style[mxConstants.STYLE_SPACING_LEFT];
    style[mxConstants.STYLE_SPACING_RIGHT] = '55';
    graph.getStylesheet().putCellStyle('left', style);
};