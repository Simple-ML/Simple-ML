//node_modules
import React, { Component } from 'react';
//React.Components
import TextEditorWrapper from './TextEditorWrapper'
//style
import './textEditor.scss';
import background from './../../../styles/background.module.scss'
import ReactDOM from "react-dom";

class TextEditor extends Component {

    constructor(props) {
        super(props);

        this.editorRef = React.createRef();
    }

    componentDidMount() {
        let container = ReactDOM.findDOMNode(this.editorRef.current);
        container.appendChild(TextEditorWrapper.editorDiv[0]);
    }

    render() {
        return (
            <div className={`textEditor-placeholder ${background.text-background}`} ref={this.editorRef}></div>
        );
    }
}

export default TextEditor;
