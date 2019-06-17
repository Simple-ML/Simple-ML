//node_modules
import React from 'react';
//React.Components
import EditorHeader from './editorHeader/EditorHeader'
import GraphicalEditor from './graphicalEditor/GraphicalEditor'
import TextEditor from './textEditor/TextEditor'
import EditorSwitch from './editorSwitch/EditorSwitch'
//serivces
import XtextServices from './../../ServerConnection/xtextServices';
//style
import './editorView.scss'


class EditorView extends React.Component {
    constructor() {
        super();
        this.state = {
            displayGraphicalEditor: 'block',
            displayTextEditor: 'block'
        }
    }

    switchView = (isGraphical) => {
        if (isGraphical === 'true'){
            this.setState({ displayTextEditor: 'none' })
        }
        else {
            this.setState({ displayTextEditor: 'block' })
        }
    }

    render() {
        //TODO: get rid of styles at this place
        let { displayGraphicalEditor, displayTextEditor } = this.state;
        let switchView = (isGraphical) => this.switchView(isGraphical);

        return(
            <div className='EditorView'>
                <EditorHeader/>
                <div className={ 'buttons' }>
                    <button style={{ color: 'white' }} onClick={ () => { XtextServices.getEmfModel(); }}>
                        { 'Get EMF-Model' }
                    </button>
                    <button style={{ color: 'white' }} onClick={ () => { XtextServices.creatableEntityProposals(); }}>
                        { 'Get Proposals' }
                    </button>
                </div>
                <div className='ide-container' >
                    <div className='view-toggler' style={{ display: 'inline-block' }}>
                        <EditorSwitch style={{ display: 'inline-block' }} switchView={ switchView }/>
                    </div>
                    <div className={ 'textEditor' } style={{  display: displayTextEditor  }}>
                        <TextEditor/>
                    </div>
                    <div className={ 'graphicalEditor' } style={{ display: displayGraphicalEditor }}>
                        <GraphicalEditor name='graph-container'/>
                    </div>
                </div>
            </div>
        )
    }
}

export default EditorView;