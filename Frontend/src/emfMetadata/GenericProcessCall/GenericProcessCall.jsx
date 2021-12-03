import React from 'react';

import store from '../../reduxStore';
import mouseDataWrapper from '../../mouse'

import { entitySelect } from '../../reducers/graphicalEditor';
import { openContextMenu } from '../../reducers/contextMenu';

import XtextServices from '../../serverConnection/XtextServices'
import EmfModelHelper from '../../helper/EmfModelHelper';

import MxGraphVertexComponent from '../../components/EditorView/GraphicalEditor/MxGraphVertexComponent';
import icon from '../../images/graph/Model/selected.svg';
import iconSelected from '../../images/graph/Model/Primary.svg';

import genericProcessCallStyle from './genericProcessCall.module.scss';

/**
 * Lookup MxGraphVertexComponent.js
 */
export default class GenericProcessCall extends MxGraphVertexComponent {

    portSize = 10;
    portOffset = 3;

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
    
    onStoreChange = (state) => {
        const emfPath = this.getEmfRef(this.props.emfEntity)
        return {
            selected: state.graphicalEditor.entitySelected.id === this.props.emfEntity.id,
            metadata: state.emfModel.processMetadata.find((element) => {
                return element.emfPath === emfPath
            })
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

    filterComplexInputsOrOutputs = (inputsOrOutputs) => {
        const result = [];

        if(inputsOrOutputs !== undefined) {
            inputsOrOutputs.forEach((item, index) => {
                let type = item.type.split('.');
                type = type[type.length - 1];

                switch(type) {
                    case 'String':
                    case 'Int':
                        return;
                    default:
                        result.push({ index, item });
                }
            })
        }

        return result;
    }

    calculatePortPosition = (iconSize, maxPorts, portIndex) => {
        if(maxPorts === 0) {
            return { x: 0, y: 0 };
        }

        return {
            x: (iconSize / (maxPorts + 1)) * (portIndex + 1),
            y: Math.sin(Math.acos((portIndex + 1) / (maxPorts + 1) - 0.5)) * (iconSize / 2)
        }
        
    }

    setIcon() {
        if(!this.state.selected) {
            return icon;
        } else {
            return iconSelected;
        }
    }

    render() {
        let complexInputs = [];
        let complexOutputs = [];

        if(this.state.metadata?.input !== undefined) {
            complexInputs = this.filterComplexInputsOrOutputs(this.state.metadata.input)
        }
        if(this.state.metadata?.input !== undefined) {
            complexOutputs = this.filterComplexInputsOrOutputs(this.state.metadata.output)
        }
        const iconSize = this.props.emfEntity.metadata.mxGraphMetadata.height > this.props.emfEntity.metadata.mxGraphMetadata.width ?
            this.props.emfEntity.metadata.mxGraphMetadata.height : this.props.emfEntity.metadata.mxGraphMetadata.width;

        return(
            <div className={genericProcessCallStyle.IconContainer}>
                <img src={this.setIcon()} alt={''} onClick={
                    () => {
                        this.entitySelect(this.props.emfEntity);
                        // this.openContextMenu({
                        //     vertex: true,
                        //     emfReference: this.props.emfEntity
                        // }, mouseDataWrapper.data.x, mouseDataWrapper.data.y);
                    }
                }/>
                <div className={genericProcessCallStyle.IconLabel}>
                    {this.state.metadata ? this.state.metadata.name : ''}
                </div>
                {   // input-ports
                    complexInputs.map((item, index) => {
                        const position = this.calculatePortPosition(iconSize, complexInputs.length, index);

                        return <div 
                            key={index} 
                            className={genericProcessCallStyle.Port} 
                            style={{
                                left: position.x - this.portSize / 2,
                                top: iconSize / 2 - position.y - this.portSize / 2 - this.portOffset,
                                height: this.portSize,
                                width: this.portSize
                            }}
                            >
                            </div>
                    })
                }
                
                {   // output-ports
                    complexOutputs.map((item, index) => {
                        const position = this.calculatePortPosition(iconSize, complexOutputs.length, index);

                        return <div 
                            key={index} 
                            className={genericProcessCallStyle.Port} 
                            style={{
                                left: position.x - this.portSize / 2,
                                top: iconSize / 2 + position.y - this.portSize / 2 + this.portOffset,
                                height: this.portSize,
                                width: this.portSize
                            }}
                            onClick={() => {
                                XtextServices.getProcessProposals(
                                    this.props.emfEntity.id, 
                                    this.state.metadata.emfPath + '/@resultList/@results.' + index
                                )
                                let associationTargetPath = '//' + EmfModelHelper.getFullHierarchy2(this.props.emfEntity)
                                associationTargetPath = associationTargetPath.substring(0, associationTargetPath.length - 1)
                                this.openContextMenu({
                                    vertex: true,
                                    emfReference: this.props.emfEntity,
                                    associationTargetPath: associationTargetPath
                                }, mouseDataWrapper.data.x, mouseDataWrapper.data.y);
                            }}
                            >
                            </div>
                    })
                }
            </div>
        )
    }
}
