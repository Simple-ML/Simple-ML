//node_modules
import React, { Component } from 'react';
//React.Components
import TextEditorWrapper from './TextEditorWrapper'
//style
import './textEditor.css';
class TextEditor extends Component {

    componentDidMount() {
        let div = window.jQuery('.textEditor-Placeholder', document);
        div.append(TextEditorWrapper.editorDiv);
    }

    render() {
        return (
            <div className={'textEditor-Placeholder'}></div>
        );
    }
}

export default TextEditor;
