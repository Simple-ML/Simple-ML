import React from 'react';
import EmfModelHelper from '../../helper/EmfModelHelper';

import MxGraphVertexComponent from '../../components/EditorView/GraphicalEditor/MxGraphVertexComponent';
import logo from '../../images/graph/instances/dataset.svg';


/**
 * Lookup MxGraphVertexComponent.js
 */
export default class GenericDataSet extends MxGraphVertexComponent {

    constructor(props) {
        super(props);
    }
    
    render() {
        return(
            <div style={{height: "36px", width: "48px"}}>
                <img src={logo}/>
            </div>
        )
    }
}