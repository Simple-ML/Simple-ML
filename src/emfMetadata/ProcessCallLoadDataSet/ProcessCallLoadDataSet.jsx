import React from 'react';
import GenericProcessCall from '../GenericProcessCall/GenericProcessCall';
import icon from '../../images/graph/instances_mvp/flow-load-dataset-loaded.svg';


export default class ProcessCallLoadDataSet extends GenericProcessCall {
 
    constructor(props) {
        super(props);
    }

    render() {
        return(
            <div style={{height: "48px", width: "48px"}}>
                <img src={icon}/>
            </div>
        )
    }
}