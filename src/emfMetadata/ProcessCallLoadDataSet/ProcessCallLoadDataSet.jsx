import React from 'react';
import GenericProcessCall from '../GenericProcessCall/GenericProcessCall';
import icon from '../../images/graph/Load Dataset/primary.svg';


export default class ProcessCallLoadDataSet extends GenericProcessCall {
 
    constructor(props) {
        super(props);
    }

    render() {
        return(
            <div>
                <img src={icon}/>
            </div>
        )
    }
}