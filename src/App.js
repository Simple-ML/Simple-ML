import React from 'react';
import './App.scss';
import GraphComponent from './components/GraphicalEditor/GraphComponent'
import XtextServices from './ServerConnection/xtextServices';

import TextEditor from './components/TextEditor/TextEditor';
import EditorSwitch from './components/EditorSwitch'
import Header from "./components/Header"

class App extends React.Component {
    constructor(){
        super();
        this.state={
            view:"graphical"
        }
    }
    renderEditor(){
        const {view}= this.state;
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
        switch(view){
            case "graphical":
                return(    
                    <div className={'graphicalEditor'} style={styleGraphicalEditor}>
                        <GraphComponent name="graph-container"/>
                    </div>)

            case "textual":
                return (
                    <div className={'textEditor'} style={styleTextEditor}>
                        <TextEditor />
                    </div>)
            default:
                return <div/>
        }

    }
    render() {
        //TODO: get rid of styles at this place
 
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
                <Header/>
                <div className="ide-container">
                    <div className="view-toggler" style={{ display:"inline-block"}}>
                        <EditorSwitch style={{ display:"inline-block"}}/>
                    </div>
                    {this.renderEditor()}
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
