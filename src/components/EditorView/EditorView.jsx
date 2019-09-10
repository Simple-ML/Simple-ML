//node_modules
import React from 'react';
import ReactReduxComponent from '../../helper/ReactReduxComponent';
//React.Components
import EditorHeader from './EditorHeader/EditorHeader'
import GraphicalEditor from './GraphicalEditor/GraphicalEditor'
import TextEditor from './TextEditor/TextEditor'
import DetailsEditor from './DetailsEditor/DetailsEditor'
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
            myLayout: null
        };

        this.flipGraph = this.flipGraph.bind(this)
    }

    switchView = (isGraphical) => {
        if (isGraphical === 'true') {
            this.setState({ displayTextEditor: 'none' })
        }
        else {
            this.setState({ displayTextEditor: 'block' })
        }
    };

    flipGraph = () => {
        this.dispatchReduxAction(changeDirection());
    };

    render() {
        return(
            <div className='EditorView'>
                <EditorHeader />
                <div className={'buttons'}>
                    <button style={{ color: 'black' }} onClick={() => { XtextServices.getEmfModel(); }}>
                        {'Get EMF-Model'}
                    </button>
                    <button style={{ color: 'black' }} onClick={this.flipGraph}>
                        { 'Flip The Graph' }
                    </button>
                </div>
                <div className='ide-container'>
                    <GoldenLayoutComponent
                        htmlAttrs={{ style: { minHeight: "780px", width: "100%" } }}
                        config={{
                            dimensions:{
                                headerHeight: "100%",
                                headerWidth: "24px"
                            },
                            content:[{
                                type: "row",
                                content: [
                                    {
                                        title: "Graphical Editor",
                                        type: "react-component",
                                        component: "graphicalEditor"
                                    },
                                    {
                                        type: 'column',
                                        content:[
                                            {                                           
                                                title: "DSL Editor",
                                                type: "react-component",
                                                component: "textEditor",
                                                height: 68.803
                                            },
                                            {
                                                title: "Details",
                                                type: "react-component",
                                                component: "detailsEditor",
                                            }
                                    ]
                                }]
                            }]
                        }}
                        registerComponents={myLayout => {
                            myLayout.registerComponent("textEditor", TextEditor);
                            myLayout.registerComponent("graphicalEditor", GraphicalEditor);
                            myLayout.registerComponent("detailsEditor", DetailsEditor);
                            this.setState({myLayout})
                        }}
                    />
                </div>
            </div>
        )
    }
}

export default EditorView;
