import React from 'react';
import { connect } from 'react-redux';
import './App.scss';
import EditorView from './components/EditorView/EditorView';


class App extends React.Component {
    render() {
        return (
            <div className="App">
                <EditorView/>
            </div>
        );
    }
}

export default connect()(App);
