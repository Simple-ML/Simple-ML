import React from 'react';
import store from '../../reduxStore';
import EmfModelHelper from '../../helper/EmfModelHelper';

import MxGraphVertexComponent from '../../components/EditorView/GraphicalEditor/MxGraphVertexComponent';
import metadata from './GenericDataSet.metadata';
import iconEmpty from '../../images/graph/File/empty.svg';
import iconEmptyHover from '../../images/graph/File/emptyHover.svg';
import iconFilled from '../../images/graph/File/filled.svg';
import iconFilledHover from '../../images/graph/File/filledHover.svg';

/**
 * Lookup MxGraphVertexComponent.js
 */
export default class GenericDataSet extends MxGraphVertexComponent {

    constructor(props) {
        super(props);

        this.state = {
            selected: false,
            empty: true,
            hoveredOver: false
        }
        store.subscribe(() => {
            this.onStoreChange(store.getState());
        });
    }

    onStoreChange = (state) => {
        this.setState({
            selected: state.graphicalEditor.entitySelected.id === this.props.emfEntity.id,
            hoveredOver: state.graphicalEditor.entityHoveredOver.id === this.props.emfEntity.id,
            empty: state.runtime.placeholder[this.props.emfEntity.data.name] === undefined
        });
    }

    setIcon() {
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

    render() {
        return(
            <div>
                <img src={this.setIcon()} alt={this.props.emfEntity.data.name}/>
            </div>
        )
    }
}

