import React from 'react';
import store from '../../reduxStore';

import MxGraphVertexComponent from '../../components/EditorView/GraphicalEditor/MxGraphVertexComponent';
import icon from '../../images/graph/Model/selected.svg';
import iconSelected from '../../images/graph/Model/Primary.svg';


/**
 * Lookup MxGraphVertexComponent.js
 */
export default class GenericProcessCall extends MxGraphVertexComponent {

    constructor(props) {
        super(props);

        this.setIcon = this.setIcon.bind(this);

        store.subscribe(() => {
            this.setState(this.onStoreChange(store.getState()));
        });
        this.state = this.onStoreChange(store.getState());
    }

    onStoreChange = (state) => {
        return {
            selected: state.graphicalEditor.entitySelected.id === this.props.emfEntity.id
        };
    }
    calculateInputPortData(parentVertex) {
        let portDataContainer = [];

        // this.props.emfEntity.children.forEach((element, index) => {
        //     let text = '';                       // TODO: get portname from emfEntity -> ProcessCallDefinition
        //     let sizeX = 16;
        //     let sizeY = 16;

        //     // position is relative to parentVertex.geometry.width -> the positionrange is 0.0 - 1.0
        //     let posX = (parentVertex.geometry.width / (this.props.emfEntity.children.length + 1) * (index + 1) - (sizeX / 2)) / parentVertex.geometry.width;
        //     let posY = -(sizeY / 2) / parentVertex.geometry.height;
        //     let emfEntity = element;
            
        //     portDataContainer.push({ 
        //         text, sizeX, sizeY, posX, posY, emfEntity
        //     });    
        // });
        return portDataContainer;
    }

    setIcon() {
        if(!this.state.selected) {
            return icon;
        } else {
            return iconSelected;
        }
    }

    render() {
        return(
            <div>
                <img src={this.setIcon()}/>
            </div>
        )
    }
}