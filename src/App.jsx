import React from 'react';
import { connect } from 'react-redux';
import './App.scss';
import EditorView from './components/EditorView/EditorView';
import Toolbar from "./components/core/Toolbar/Toolbar";


class App extends React.Component {
    render() {
        return (
            <div className="App">
                <EditorView/>

                <Toolbar/>
            </div>
        );
    }
}

export default connect()(App);
