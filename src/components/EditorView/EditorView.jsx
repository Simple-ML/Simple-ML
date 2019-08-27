//node_modules
import React from 'react';
import ReactReduxComponent from '../../helper/ReactReduxComponent';
//React.Components
import EditorHeader from './EditorHeader/EditorHeader'
import GraphicalEditor from './GraphicalEditor/GraphicalEditor'
import TextEditor from './TextEditor/TextEditor'
import { GoldenLayoutComponent } from './../../helper/goldenLayoutServices/goldenLayoutComponent';

import XtextServices from '../../serverConnection/XtextServices';
import { changeDirection } from '../../reducers/graphicalEditor';
//style
import './editorView.scss'
import 'golden-layout/src/css/goldenlayout-base.css';


class EditorView extends ReactReduxComponent {
    constructor() {
        super();
        this.state = {
            displayGraphicalEditor: 'block',
            displayTextEditor: 'block',
            isVertical:'true',
            myLayout: null
        }

        this.flipGraph = this.flipGraph.bind(this)
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

        this.dispatchReduxAction(changeDirection());
        var { isVertical } = this.state;
        if (isVertical === 'true'){
            this.setState({ isVertical: 'false' })
        }
        else {
            this.setState({ isVertical: 'true' })
        }
    }

    render() {
        return(
            <div className='EditorView'>
                <EditorHeader/>
                <div className={ 'buttons' }>
                    <button style={{ color: 'black' }} onClick={ () => { XtextServices.getEmfModel(); }}>
                        { 'Get EMF-Model' }
                    </button>
                    <button style={{ color: 'black' }} onClick={ this.flipGraph }>
                        { 'Flip The Graph' }
                    </button>
                </div>
                <div className='ide-container'>
                    <GoldenLayoutComponent
                        htmlAttrs={{ style: { height: "500px", width: "100%" } }}
                        config={{
                            content:[{
                                type: "row",
                                content: [{
                                        title: "DSL Editor",
                                        type: "react-component",
                                        component: "textEditor",
                                    },
                                    {
                                        title: "Graphical Editor",
                                        type: "react-component",
                                        component: "graphicalEditor",
                                    }]
                            }]
                        }}
                        registerComponents={myLayout => {
                            myLayout.registerComponent("textEditor", TextEditor);
                            myLayout.registerComponent("graphicalEditor", GraphicalEditor)
                            this.setState({myLayout})
                        }}
                    />
                </div>
            </div>
        )
    }
}

export default EditorView;
