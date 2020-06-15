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
     * Must be implemented from derived class.
     * 
     * @returns [{
     *      text: string,
     *      posX: integer,
     *      posY: integer,
     *      sizeX: integer,
     *      sizeY: integer
     * },
     * ...]
     */
    calculateInputPortData() {
        return [];
    }

    /**
     * See calculateInputPortData() definition
     */
    calculateOutputPortData() {
        return [];
    }

    /**
     * @param graphReference:       mxGraph 
     * @param vertex:               mxGraph.Cell
     */
    attachePorts(graphReference, vertex) {
        var portData = this.calculateInputPortData();

        for(var port of portData) {
            var port = graphReference.insertVertex(vertex, null, portText, portPosition.x, portPosition.y, portSize, portSize, 
                'port;image=editors/images/overlays/check.png;align=right;imageAlign=right;spacingRight=18', true);
            port.geometry.offset = new mxPoint(-(portSize / 2), -(portSize /2));
        }
    }

    renderToMxGraph(graphReference) {
        const placeholderDiv = document.getElementById("mxReactPlaceholder");
        ReactDOM.render(this.render(), placeholderDiv, () => {
            const renderedDiv = placeholderDiv.childNodes[0];
            const renderedDivRect = renderedDiv.getBoundingClientRect();

            var vertex = graphReference.insertVertex(graphReference.getDefaultParent(), null, '', 0, 0, renderedDivRect.width, renderedDivRect.height);
            vertex.contentDiv = renderedDiv;
            this.attachePorts(graphReference, vertex);
        });

    }
}