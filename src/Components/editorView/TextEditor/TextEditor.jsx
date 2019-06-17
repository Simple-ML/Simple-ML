import React, { Component } from 'react';
import './textEditor.css';

import TextEditorWrapper from './TextEditorWrapper'

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
