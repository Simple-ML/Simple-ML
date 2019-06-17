import React from 'react';
import Header from './EditorHeader/EditorHeader'
import GraphicalEditor from './GraphicalEditor/GraphicalEditor'
import TextEditor from './TextEditor/TextEditor'
import EditorSwitch from './EditorSwitch/EditorSwitch'
import XtextServices from './../../ServerConnection/xtextServices';

import './EditorView.scss'

class EditorView extends React.Component {
    constructor() {
        super();
        this.state = {
            displayGraphicalEditor: 'block',
            displayTextEditor: 'block'
        }
    }

    switchView = (isGraphical) => {
        if ( isGraphical === "true" ){
            this.setState({ displayTextEditor: 'none' })
        }
        else {
            this.setState({ displayTextEditor: 'block' })
        }
    }

    render() {
        //TODO: get rid of styles at this place
        let { displayGraphicalEditor, displayTextEditor } = this.state;
        let styleButtons = {
            display: 'inline',
            position: 'relative',
            width: '450px',
            margin: '20px', 
        };
        let styleTextEditor = {
            display: displayTextEditor,
            position: 'relative',
            minWidth: '450px',
            minHeight: '400px',
            margin: '0px'
        };
        let styleGraphicalEditor = {
            display: displayGraphicalEditor,
            position: 'relative',
            minWidth: '450px',
            minHeight: '400px',
            marginTop: '20px'
        };
        let switchView = (isGraphical) => this.switchView(isGraphical);
        return(
            <div className = "EditorView">
                <Header/>
                <div className = { 'buttons' } style = { styleButtons } >
                    <button style = {{ color: 'white' }} onClick = { () => { XtextServices.getEmfModel(); }}>
                        { 'Get EMF-Model' }
                    </button>
                    <button style = {{ color: 'white' }} onClick = { () => { XtextServices.creatableEntityProposals(); }}>
                        { 'Get Proposals' }
                    </button>
                </div>
                <div className = "ide-container" >
                    <div className = "view-toggler" style = {{ display: "inline-block" }}>
                        <EditorSwitch style = {{ display: "inline-block" }} switchView = { switchView }/>
                    </div>
                    <div className = { 'textEditor' } style = { styleTextEditor }>
                        <TextEditor/>
                    </div>
                    <div className = { 'graphicalEditor' } style = { styleGraphicalEditor }>
                        <GraphicalEditor name = "graph-container" />
                    </div>
                </div>
            </div>
        )
    }
}

export default EditorView;