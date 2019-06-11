import React from 'react';
import './App.css';

import XtextServices from './ServerConnection/xtextServices';

import TextEditor from './Components/TextEditor/TextEditor';

class App extends React.Component {

    render() {

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
        }

        XtextServices.addSuccessListener((serviceType, result) => {
            console.log({serviceType, result});
        });

        return (
            <div className="App">
                <header className="App-header">

                </header>
                <div className={'graphicalEditor'} style={styleGraphicalEditor}>

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
                    <button onClick={() => { XtextServices.getEntityAttributes([{className:'org.xtext.example.mydsl.myDsl.ElementCollection'}]); }}>
                        {'Get Entityattributes'}
                    </button>
                </div>
            </div>
        );
    }
}

export default App;
