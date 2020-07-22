import React from 'react';
import EmfModelHelper from '../../helper/EmfModelHelper';

import MxGraphComponent from '../../components/EditorView/GraphicalEditor/MxGraphVertexComponent';
import logo from '../../images/graph/instances/dataset.svg';


/**
 * Lookup MxGraphVertexComponent.js
 */
export default class GenericProcessCall extends MxGraphComponent {

    constructor(props) {
        super(props);
    }

    calculateInputPortData(parentVertex) {
        var portDataContainer = [];

        this.props.emfEntity.children.forEach((element, index) => {
            var text = '';                       // TODO: get portname from emfEntity -> ProcessCallDefinition
            var sizeX = 16;
            var sizeY = 16;
            var posX = (parentVertex.geometry.width / this.props.emfEntity.children.length + 1) * (index + 1) - (sizeX / 2);
            var posY = -(sizeY / 2);
            var emfPath = EmfModelHelper.getFullHierarchy2(element);
            
            portDataContainer.push({ 
                text, sizeX, sizeY, posX, posY, emfPath
            });    
        });
        return portDataContainer;
    }

    render() {
        return(
            <div style={{height: "100px", width: "100px", background: "red"}}>
                <img style={{"paddingTop":"10px"}} src={logo} />
                <button onClick={()=> {console.log('hello')}}>hello</button>
            </div>
        )
    }
}