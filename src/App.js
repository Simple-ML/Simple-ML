import React from 'react';
import './App.css';
import XtextServices from "./ServerConnection/xtextServices";

import JsonComponent from './JsonComponent'

class App extends React.Component {

    constructor() {
        super()

        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                console.log(result);
            }
        });
        let temp = new JsonComponent()
    }

    render() {
        return (
            <div className="App">
                <JsonComponent/>
            </div>
        );
  }
}

export default App;
