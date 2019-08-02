//node_modules
import React from 'react';
//React.Components
import EditorHeader from './EditorHeader/EditorHeader'
import GraphicalEditor from './GraphicalEditor/GraphicalEditor'
import TextEditor from './TextEditor/TextEditor'
import EditorSwitch from './EditorSwitch/EditorSwitch'
//serivces
import XtextServices from '../../serverConnection/XtextServices';
//style
import './editorView.scss'


class EditorView extends React.Component {
    constructor() {
        super();
        this.state = {
            displayGraphicalEditor: 'block',
            displayTextEditor: 'block',
            isVertical:'true'
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

    flipGraph = () =>{
        var { isVertical } = this.state;
        if (isVertical === 'true'){
            this.setState({ isVertical: 'false' })
        }
        else {
            this.setState({ isVertical: 'true' })
        }
    }

    render() {
        //TODO: get rid of styles at this place
        let { displayGraphicalEditor, displayTextEditor, isVertical } = this.state;
        let switchView = (isGraphical) => this.switchView(isGraphical);
        let flipGraph = () => this.flipGraph();
        return(
            <div className='EditorView'>
                <EditorHeader/>
                <div className={ 'buttons' }>
                    <button style={{ color: 'black' }} onClick={ () => { XtextServices.getEmfModel(); }}>
                        { 'Get EMF-Model' }
                    </button>
                    <button style={{ color: 'black' }} onClick={ flipGraph }>
                        { 'Flip The Graph' }
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
                        <GraphicalEditor name='graph-container'  isVertical={isVertical}/>
                    </div>
                </div>
            </div>
        )
    }
}

export default EditorView;
