import React from 'react';
import store from '../../reduxStore';

import MxGraphVertexComponent from '../../components/EditorView/GraphicalEditor/MxGraphVertexComponent';

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
                <img src={this.setIcon()} alt={this.props.emfEntity.data.name}/>
                <div className={genericDataSetStyle.IconLabel}>
                    {this.props.emfEntity.data.name}
                </div>
            </div>
        )
    }
}

