import React, { Component } from 'react';

import TextEditorWrapper from './TextEditorWrapper'

class TextEditor extends Component {
/*
    textEditorStyle = {
        display: 'block',
        position: 'absolute',
        top: 0,
        bottom: 0,
        left: '480px',
        width: '450px',
        margin: '20px'
    };
*/
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
