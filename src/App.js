import React from 'react';
import './App.scss';
import XtextServices from './serverConnection/XtextServices';
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

export default App;
