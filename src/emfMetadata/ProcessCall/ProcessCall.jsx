import React from 'react';
import MxGraphComponent from '../../components/mxGraphComponent/MxGraphComponent';
import logo from '../../images/graph/instances/dataset.svg';

export default class ProcessCall extends MxGraphComponent {

    constructor(props) {
        super(props);
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