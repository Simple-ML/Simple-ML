import React from 'react';

import store from '../../reduxStore';
import mouseDataWrapper from '../../mouse'

import { entitySelect } from '../../reducers/graphicalEditor';
import { openContextMenu } from '../../reducers/contextMenu';

import MxGraphVertexComponent from '../../components/EditorView/GraphicalEditor/MxGraphVertexComponent';
import icon from '../../images/graph/Model/selected.svg';
import iconSelected from '../../images/graph/Model/Primary.svg';

import genericProcessCallStyle from './genericProcessCall.module.scss';

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
        this.state = {
            selected: false,
            metadata: {}
        };
    }
    
    entitySelect = (entity) => { 
        store.dispatch(entitySelect(entity)) ;
    }

    openContextMenu = (context, x, y) => {
        store.dispatch(openContextMenu(context, x, y));
    }

    getEmfRef = (emfEntity) => {
        if(emfEntity.getChild('@member')) {
            return this.getEmfRef(emfEntity.getChild('@member'))
        } else if(emfEntity.getChild('@receiver')) {
            return this.getEmfRef(emfEntity.getChild('@receiver'))
        } else if(emfEntity.getChild('@declaration')){
            return emfEntity.getChild('@declaration').data['$ref']
        }
    }

    onStoreChange = (state) => {
        const emfPath = this.getEmfRef(this.props.emfEntity)
        return {
            selected: state.graphicalEditor.entitySelected.id === this.props.emfEntity.id,
            metadata: state.emfModel.processMetadata.find((element) => {
                return element.emfPath === emfPath
            })
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
            <div className={genericProcessCallStyle.IconContainer}>
                <img src={this.setIcon()} alt={''} onClick={
                    () => {
                        this.entitySelect(this.props.emfEntity);
                        this.openContextMenu({
                            vertex: true,
                            emfReference: this.props.emfEntity
                        }, mouseDataWrapper.data.x, mouseDataWrapper.data.y);
                    }
                }/>
                <div className={genericProcessCallStyle.Port} onClick={() => console.log('hello')}></div>
                <div className={genericProcessCallStyle.IconLabel}>
                    {this.state.metadata ? this.state.metadata.name : ''}
                </div>
            </div>
        )
    }
}
