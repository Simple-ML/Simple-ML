import React from 'react';
import './App.css';
import XtextServices from "./ServerConnection/xtextServices";

import GraphComponent from './GraphComponent'

class App extends React.Component {

    constructor() {
        super()

        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {

            }
        });

    }

    render() {
        return (
            <div className="App">
                <GraphComponent />
            </div>
        );
    }
}

export default App;
