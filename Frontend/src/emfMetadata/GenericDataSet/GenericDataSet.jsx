import React from 'react';
import store from '../../reduxStore';

import { entitySelect } from '../../reducers/graphicalEditor';
import { openContextMenu } from '../../reducers/contextMenu';

import MxGraphVertexComponent from '../../components/EditorView/GraphicalEditor/MxGraphVertexComponent';

import mouseDataWrapper from '../../mouse'
import XtextServices from '../../serverConnection/XtextServices'
import EmfModelHelper from '../../helper/EmfModelHelper';

import iconEmpty from '../../images/graph/File/empty.svg';
import iconEmptyHover from '../../images/graph/File/emptyHover.svg';
import iconFilled from '../../images/graph/File/filled.svg';
import iconFilledHover from '../../images/graph/File/filledHover.svg';

import genericDataSetStyle from './genericDataSet.module.scss';

/**
 * Lookup MxGraphVertexComponent.js
 */
export default class GenericDataSet extends MxGraphVertexComponent {

    constructor(props) {
        super(props);

        this.onStoreChange = this.onStoreChange.bind(this);
        this.setIcon = this.setIcon.bind(this);

        this.unsbuscribe = store.subscribe(() => {
            this.setState(this.onStoreChange(store.getState()));
        });

        this.state = this.onStoreChange(store.getState());
    }

    onStoreChange = (state) => {
        return {
            selected: state.graphicalEditor.entitySelected.id === this.props.emfEntity.id,
            hoveredOver: state.graphicalEditor.entityHoveredOver.id === this.props.emfEntity.id,
            empty: state.runtime.placeholder[this.props.emfEntity.data.name] === undefined
        };
    }

    entitySelect = (entity) => { 
        store.dispatch(entitySelect(entity)) ;
    }

    openContextMenu = (context, x, y) => {
        store.dispatch(openContextMenu(context, x, y));
    }

    setIcon = () => {
        if(this.state.empty) {
            if(this.state.hoveredOver) {
                return iconEmptyHover;
            } else {
                return iconEmpty;
            }
        } else {
            if(this.state.hoveredOver) {
                return iconFilledHover;
            } else {
                return iconFilled;
            }
        }
    }

    componentWillUnmount() {
        this.unsubscribe();
    }

    render() {
        return(
            <div className={genericDataSetStyle.IconContainer}>
                <img src={this.setIcon()} alt={this.props.emfEntity.data.name}
                onClick={() => {
                    let associationTargetPath = EmfModelHelper.getFullHierarchy(this.props.emfEntity)
                    XtextServices.getProcessProposals(
                        this.props.emfEntity.id, 
                        associationTargetPath
                    )
                    
                    this.openContextMenu({
                        vertex: true,
                        emfReference: this.props.emfEntity,
                        associationTargetPath: associationTargetPath
                    }, mouseDataWrapper.data.x, mouseDataWrapper.data.y);
                }}
                />
                <div className={genericDataSetStyle.IconLabel}>
                    {this.props.emfEntity.data.name}
                </div>
            </div>
        )
    }
}

