import React from 'react';
import ReactDOM from 'react-dom';
import { mxPoint } from 'mxgraph-js';


export default class MxGraphComponent extends React.Component {

    /**
     * 
     * @param props: {
     *      emfEntity: {},
     *      globalConfig: {},
     *      graphConfig: {}
     * } 
     */
    constructor(props) {
        super(props);
    }

    /**
     * Must be implemented from derived class.
     * 
     * @returns [{
     *      text: string,
     *      posX: integer,
     *      posY: integer,
     *      sizeX: integer,
     *      sizeY: integer,
     *      emfPath: string     // path of entity in emf-tree for association in backend
     * },
     * ...]
     */
    calculateInputPortData(parentVertex) {
        return [];
    }

    /**
     * See calculateInputPortData() definition
     */
    calculateOutputPortData(parentVertex) {
        return [];
    }

    /**
     * @param graphReference:       mxGraph 
     * @param vertex:               mxGraph.Cell
     */
    attachePorts(graphReference, vertex) {
        let portsDataContainer = this.calculateInputPortData(vertex);

        for(let portData of portsDataContainer) {
            let port = graphReference.insertVertex(vertex, null, portData.text, portData.posX, portData.posY, portData.sizeX, portData.sizeY, 
                'port;image=editors/images/overlays/check.png;align=right;imageAlign=right;spacingRight=18', true);
            port.portData = {
                text: portData.text,
                emfPath: portData.emfPath
            }
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