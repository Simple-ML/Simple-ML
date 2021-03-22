import React from 'react';
import { connect } from 'react-redux';
import './App.scss';
import EditorView from './components/EditorView/EditorView';
import LoginView from './components/LoginView/LoginView';
import ChooseProjectView from './components/ChooseProjectView/ChooseProjectView';
import ContextMenu from './components/core/ContextMenu/ContextMenu';
import ModalContainer from './components/core/Modal/ModalContainer';
import XtextServices from './serverConnection/XtextServices';

class App extends React.Component {
    render() {
        return (
            <div className="App">
                <button onClick={XtextServices.generate}>generate</button>
                <EditorView/>

                <ContextMenu/>
                <ModalContainer/>
            </div>
        );
    }
}

export default connect()(App);
