import React from 'react';
import './App.css';

import XtextServices from './ServerConnection/xtextServices';

import TextEditor from './Components/TextEditor/TextEditor';

class App extends React.Component {

    render() {

        let styleLeft = {
            display: 'block',
            position: 'absolute',
            top: 0,
            bottom: 0,
            left: 0,
            width: '450px',
            margin: '20px'
        };
        let styleRight = {
            display: 'block',
            position: 'absolute',
            top: 0,
            bottom: 0,
            left: '480px',
            width: '450px',
            margin: '20px'
        };

         return (
            <div className="App">
                <header className="App-header">

                </header>
                <div style={styleLeft}>
                    <TextEditor />
                </div>
                <div style={styleRight}>
                    <button onClick={() => { XtextServices.getEmfModel(); }}>
                        {'Get EMF-Model'}
                    </button>
                </div>
            </div>
        );
  }
}

export default App;
