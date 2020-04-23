import React from 'react';
import ReactDOM from 'react-dom';
import { mxPoint } from 'mxgraph-js';


export default class MxGraphComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            emfEntity: {},
            globalConfig: {},
            graphConfig: {}
        };
    }

    /**
     * @param graphReference:       mxGraph 
     * @param vertex:               mxGraph.Cell
     */
    attachePorts = (graphReference, vertex) => {
        this.props.emfEntity.children.forEach((element, index) => {
            const portSize = 16;
            const portSizeRelativToVertex = portSize / vertex.geometry.width;
            const portText = '' + index;
            const portPosition = this.calculateRelativePortPosition(
                    index, 
                    this.props.emfEntity.children.length, 
                    portSizeRelativToVertex / 2, 
                    portSizeRelativToVertex / 2
                );
console.log(portPosition)

            var port = graphReference.insertVertex(vertex, null, portText, portPosition.x, portPosition.y, portSize, portSize, 
                'port;image=editors/images/overlays/check.png;align=right;imageAlign=right;spacingRight=18', true);
                port.geometry.offset = new mxPoint(-(portSize / 2), -(portSize /2));
        });
    }

    calculateRelativePortPosition = (index, maxIndex, offsetX, offsetY) => {
        if(maxIndex === 0) return { x: 0, y: 0 }

        // if vertikal alignment
        return {
            x: (index + 1) / (maxIndex + 1),
            y: 0
        }
        // if label is a circle

        // other implementations
    }

    renderToMxGraph(graphReference) {
        
        // const newDiv = document.createElement("div");
        const newDiv = document.getElementById("mxReactPlaceholder");
        ReactDOM.render(this.render(), newDiv, () => {
            const rects = newDiv.childNodes[0].getBoundingClientRect();

            var vertex = graphReference.insertVertex(graphReference.getDefaultParent(), null, '', 0, 0, rects.width, rects.height);
            vertex.fromReactComponent = newDiv;
            this.attachePorts(graphReference, vertex);
        });

    }
}