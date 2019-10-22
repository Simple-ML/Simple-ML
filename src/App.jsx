import React from 'react';
import { connect } from 'react-redux';
import './App.scss';
import EditorView from './components/EditorView/EditorView';
import LoginView from './components/LoginView/LoginView';
import ChooseProjectView from './components/ChooseProjectView/ChooseProjectView';
import Toolbar from './components/core/Toolbar/Toolbar';
import ModalContainer from './components/core/Modal/ModalContainer';

class App extends React.Component {
    render() {
        return (
            <div className="App">
                <EditorView/>

                <Toolbar/>
                <ModalContainer/>
            </div>
        );
    }
}

export default connect()(App);
