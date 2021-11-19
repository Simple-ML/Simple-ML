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
}