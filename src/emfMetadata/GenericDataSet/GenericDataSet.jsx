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
            empty: true,
            hoveredOver: false
        }
        store.subscribe(() => {
            this.onStoreChange(store.getState());
        });
    }

    onStoreChange = (state) => {
        this.setState({
            hoveredOver: state.graphicalEditor.entityHoveredOver.id === this.props.emfEntity.id
        });
    }
    componentDidMount() {
        console.log('mount')
    }
    componentWillUnmount() {
        console.log('unmount')
    }

    setIcon() {
        console.log('render', this.state)
        if(this.state.empty) {
            if(this.state.hoveredOver) {
                return <img src={iconEmptyHover}/>;
            } else {
                return <img src={iconEmpty}/>;
            }
        } else {
            if(this.state.hoveredOver) {
                return <img src={iconFilledHover}/>;
            } else {
                return <img src={iconFilled}/>;
            }
        }
    }

    render() {
        return(
            <div>
                { this.setIcon() }
            </div>
        )
    }
}

