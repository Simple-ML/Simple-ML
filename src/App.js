import React from 'react';
import './App.scss';

import GraphComponent from './GraphComponent'

class App extends React.Component {
    render() {
        return (
            <div className="App">
                <div className="app-header"></div>
                <div className="ide-container">
                <GraphComponent name="graph-container"/>
                </div>
                
            </div>
        );
    }
}

export default App;
