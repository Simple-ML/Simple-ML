import React from 'react';
import EmfModelHelper from '../../helper/EmfModelHelper';

import MxGraphVertexComponent from '../../components/EditorView/GraphicalEditor/MxGraphVertexComponent';
import logo from '../../images/graph/instances/split.svg';


/**
 * Lookup MxGraphVertexComponent.js
 */
export default class GenericProcessCall extends MxGraphVertexComponent {

    constructor(props) {
        super(props);
    }

    calculateInputPortData(parentVertex) {
        let portDataContainer = [];

        this.props.emfEntity.children.forEach((element, index) => {
            let text = '';                       // TODO: get portname from emfEntity -> ProcessCallDefinition
            let sizeX = 16;
            let sizeY = 16;

            // position is relative to parentVertex.geometry.width -> the positionrange is 0.0 - 1.0
            let posX = (parentVertex.geometry.width / (this.props.emfEntity.children.length + 1) * (index + 1) - (sizeX / 2)) / parentVertex.geometry.width;
            let posY = -(sizeY / 2) / parentVertex.geometry.height;
            let emfEntity = element;
            
            portDataContainer.push({ 
                text, sizeX, sizeY, posX, posY, emfEntity
            });    
        });
        return portDataContainer;
    }

    render() {
        return(
            <div style={{height: "36px", width: "48px"}}>
                <img src={logo}/>
            </div>
        )
    }
}