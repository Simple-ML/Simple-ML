import React from 'react';
import './App.css';
import TextEditor from './Components/TextEditor/TextEditor';

import testCode from './testCode'

class App extends React.Component {

    render() {

        //testCode();

        return (
            <div className="App">
                <header className="App-header">


                </header>
                <TextEditor />
            </div>
        );
  }
}

export default App;
