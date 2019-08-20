//node_modules
import React from 'react';

//React.Components
import EditorHeader from './EditorHeader/EditorHeader'
import GraphicalEditor from './GraphicalEditor/GraphicalEditor'
import TextEditor from './TextEditor/TextEditor'
import {GoldenLayoutComponent} from './../../helper/goldenLayoutServices/goldenLayoutComponent';
//serivces
import {AppContext} from './../../helper/goldenLayoutServices/appContext'
import XtextServices from '../../serverConnection/XtextServices';
//style
import './editorView.scss'
import 'golden-layout/src/css/goldenlayout-base.css';


class EditorView extends React.Component {
    constructor() {
        super();
        this.state = {
            displayGraphicalEditor: 'block',
            displayTextEditor: 'block',
            isVertical:'true',
            myLayout: null
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
        console.log(this.state)
    }

    render() {
        //TODO: get rid of styles at this place
        let { displayGraphicalEditor, displayTextEditor, isVertical } = this.state;
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
                <div className='ide-container'>
                    <AppContext.Provider value={this.state.isVertical}>
                        <GoldenLayoutComponent
                            htmlAttrs={{ style: { height: "500px", width: "100%" } }}
                            config={{
                                content:[{
                                    type: "row",
                                    content: [{
                                            title: "DSL Editor",
                                            type: "react-component",
                                            component: "textEditor",
                                            props: {className:'textEditor', style:{display: displayTextEditor}}
                                        },
                                        {
                                            title: "Graphical Editor",
                                            type: "react-component",
                                            component: "graphicalEditor",
                                            props:{name:'graph-container', style:{display: displayGraphicalEditor}, isVertical:isVertical}, 
                                        }]
                                }]
                            }}
                            registerComponents={myLayout => {
                                this.setState({myLayout})
                                myLayout.registerComponent("textEditor", TextEditor);
                                myLayout.registerComponent("graphicalEditor", GraphicalEditor)
                            }}
                        />
                    </AppContext.Provider>
                </div>
            </div>
        )
    }
}

export default EditorView;
