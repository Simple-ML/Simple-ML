import React from 'react';
import { connect } from 'react-redux';
import './App.scss';
import './componentCss.css';
import EditorView from './components/EditorView/EditorView';
import LoginView from './components/LoginView/LoginView';
import ChooseProjectView from './components/ChooseProjectView/ChooseProjectView';
import Toolbar from './components/core/Toolbar/Toolbar';
import ModalContainer from './components/core/Modal/ModalContainer';
import GraphicalEditor from './components/EditorView/GraphicalEditor/GraphicalEditor'

import XtextServices from './serverConnection/XtextServices';


const handler = () => {
    XtextServices.generate();
}
class App extends React.Component {
    render() {
        return (
            <div className="App">
                <button onClick={handler}>generate</button>
                <EditorView/>

                <Toolbar/>
                <ModalContainer/>
            </div>
        );
    }
}

export default connect()(App);
