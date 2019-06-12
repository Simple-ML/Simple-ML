import React from 'react';
import './App.scss';

import GraphComponent from './GraphComponent'
import EditorSwitch from './components/EditorSwitch'
import Header from "./components/Header"

class App extends React.Component {
    constructor(){
        this.state={
            view:"graphical"
        }
    }
    renderEditor(){
        const {view}= this.state;
        switch(view){
            case "graphical":
                return <GraphComponent name="graph-container"/>
            case "textual":
                return <>
        }

    }
    render() {
        return (
            <div className="App">
                <Header/>
                <div className="ide-container">
                    <div className="view-toggler" style={{ display:"inline-block"}}>
                        <EditorSwitch style={{ display:"inline-block"}}/>
                    </div>
                    <GraphComponent name="graph-container"/>
                </div>
                
            </div>
        );
    }
}

export default App;
