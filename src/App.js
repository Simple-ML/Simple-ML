import React from 'react';
import './App.scss';
import XtextServices from './ServerConnection/xtextServices';
import EditorVIew from './components/editorView/EditorView'


class App extends React.Component {
    render() {
        XtextServices.addSuccessListener((serviceType, result) => {
            console.log({ serviceType, result });
        });
        return (
            <div className="App">
                <EditorVIew/>
            </div>
        );
    }
}

export default App;
