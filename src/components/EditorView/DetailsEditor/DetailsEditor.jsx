//node_modules
import React from 'react';

//React.Components
import ReactReduxComponent from '../../../helper/ReactReduxComponent';

//style
import background from './../../../styles/background.module.scss'



class DetailsEditor extends ReactReduxComponent {
    constructor() {
        super();
        this.state = {
        };
    }

    render() {
        var details = [
            {title:"Lorem ipsum "},
            {title:"dolor sit ame"},
            {title:"adipiscing elit"},
            {title:"Aenean commodo"},
            {title:"ligula eget dolor"},
            {title:"Aenean massa"},
        ]

        return(
            <div className={`details-editor ${background.dark}`}>

            </div>
        )
    }
}

export default DetailsEditor;