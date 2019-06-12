import React from 'react';
import './App.css';

import XtextServices from './ServerConnection/xtextServices';

import TextEditor from './Components/TextEditor/TextEditor';
import GraphComponent from './Components/GraphicalEditor/GraphComponent'

class App extends React.Component {

    render() {
        //TODO: get rid of styles at this place
        let styleGraphicalEditor= {
            display: 'block',
            position: 'absolute',
            top: 0,
            bottom: 0,
            left: 0,
            width: '450px',
            margin: '20px'
        };
        let styleTextEditor = {
            display: 'block',
            position: 'absolute',
            top: 0,
            bottom: 0,
            left: '500px',
            width: '450px',
            margin: '20px'
        };
        let styleButtons = {
            display: 'block',
            position: 'absolute',
            top: 0,
            bottom: 0,
            left: '1000px',
            width: '450px',
            margin: '20px'
        };

        XtextServices.addSuccessListener((serviceType, result) => {
            console.log({serviceType, result});
        });

        return (
            <div className="App">
                <header className="App-header">

                </header>
                <div className={'graphicalEditor'} style={styleGraphicalEditor}>
                    <GraphComponent />
                </div>
                <div className={'textEditor'} style={styleTextEditor}>
                    <TextEditor />
                </div>
                <div className={'buttons'} style={styleButtons}>
                    <button onClick={() => { XtextServices.getEmfModel(); }}>
                        {'Get EMF-Model'}
                    </button>
                    <button onClick={() => { XtextServices.creatableEntityProposals(); }}>
                        {'Get Proposals'}
                    </button>
                </div>
            </div>
        );
    }
}

export default App;
