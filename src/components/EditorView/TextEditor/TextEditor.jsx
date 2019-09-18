//node_modules
import React, { Component } from 'react';
//React.Components
import TextEditorWrapper from './TextEditorWrapper'
//style
import './textEditor.scss';
import background from './../../../styles/background.module.scss'

class TextEditor extends Component {

    componentDidMount() {
        let div = window.jQuery('.textEditor-placeholder', document);
        div.append(TextEditorWrapper.editorDiv);
    }

    render() {
        return (
            <div className={`textEditor-placeholder ${background.text-background}`}></div>
        );
    }
}

export default TextEditor;
