import React from 'react';
import ReactDOM from 'react-dom';
import { mxPoint } from 'mxgraph-js';


export default class MxGraphVertexComponent extends React.Component {

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
     *      emfEntity: object     // reference to emfEntity
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
            // let port = graphReference.insertVertex(vertex, null, portData.text, portData.posX, portData.posY, portData.sizeX, portData.sizeY, 
            //     'port', true);
            
            port.portData = {
                text: portData.text
            }
            port.emfReference = portData.emfEntity;
        }
    }

    renderToMxGraph(graphReference) {
        const placeholderDiv = document.getElementById("mxReactPlaceholder");
        let placeholderDivChild = document.createElement("div");

        placeholderDiv.appendChild(placeholderDivChild);

        // Reason for Promise: In the callback of ReactDOM.render(...) i require a reference to the created DOM-Element and a reference 
        // to the class-instance (cant have them both at once).
        let promise = new Promise((resolve, reject) => {
            ReactDOM.render(this.render(), placeholderDivChild, function() {
                // this points to callback-instance (created DOM-Element) because of function() {}
                const renderedDiv = this;
                const renderedDivRect = renderedDiv.getBoundingClientRect();
console.log(renderedDivRect)
                renderedDiv.parentNode.parentNode.removeChild(renderedDiv.parentNode);
                
                var vertex = graphReference.insertVertex(graphReference.getDefaultParent(), null, '', 0, 0, renderedDivRect.width, renderedDivRect.height, 'TODO');
                vertex.contentDiv = renderedDiv;
                resolve(vertex);
            });
        });
        promise.then((vertex) => {
            // this points to class-instance because of () => {}
            this.attachePorts(graphReference, vertex);
            vertex.emfReference = this.props.emfEntity;
        });
        return promise;
    }
}