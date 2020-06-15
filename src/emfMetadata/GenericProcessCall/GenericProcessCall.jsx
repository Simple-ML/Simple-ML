import React from 'react';
import MxGraphComponent from '../../components/EditorView/GraphicalEditor/MxGraphVertexComponent';
import logo from '../../images/graph/instances/dataset.svg';

export default class GenericProcessCall extends MxGraphComponent {

    constructor(props) {
        super(props);
    }

    calculateInputPortData() {
        this.props.emfEntity.children.forEach((element, index) => {
            const portSize = 16;
            const portSizeRelativToVertex = portSize / vertex.geometry.width;
            const portText = '' + index;
            const portPosition = this.calculateRelativePortPosition(
                    index, 
                    this.props.emfEntity.children.length, 
                    portSizeRelativToVertex / 2, 
                    portSizeRelativToVertex / 2
                );
        });
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