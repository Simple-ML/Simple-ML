//node_modules
import React from 'react';


//React.Components
import EditorHeader from './../EditorView/EditorHeader/EditorHeader'

//style
import './chooseProjectView.scss'
import background from './../../styles/background.module.scss'



class ChooseProjectView extends React.Component {
    constructor() {
        super();
        this.state = {
        };
    }

    render() {
        var projects = [
            {title:"Lorem ipsum "},
            {title:"dolor sit ame"},
            {title:"adipiscing elit"},
            {title:"Aenean commodo"},
            {title:"ligula eget dolor"},
            {title:"Aenean massa"},
        ]

        return(
            <div className='ChooseProjectView'>
                <EditorHeader />
                <div class="row">
                <div className={`tutorial-container ${background.geometry}`}>

                </div>
                <div className={`project-container ${background.dark}`}>
                </div>
                </div>
            </div>
        )
    }
}

export default ChooseProjectView;