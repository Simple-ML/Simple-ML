import React from 'react';
import logo from './logo.svg';
import './App.css';
import XtextServices from "./ServerConnection/xtextServices";

import JsonComponent from './JsonComponent'

class App extends React.Component {

<<<<<<< Updated upstream
  render() {
      return (
          <div className="App">
              <header className="App-header">
                  <img src={logo} className="App-logo" alt="logo"/>
                  <p>
                      Edit <code>src/App.js</code> and save to reload.
                  </p>
                  <a
                      className="App-link"
                      href="https://reactjs.org"
                      target="_blank"
                      rel="noopener noreferrer"
                  >
                      Learn React
                  </a>
              </header>
          </div>
      );
=======
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

        testCode();

        return (
            <div className="App">

                <header className="App-header">
                    <img src={logo} className="App-logo" alt="logo"/>
                    <p>
                        Edit <code>src/App.js</code> and save to reload.
                    </p>
                    <a
                        className="App-link"
                        href="https://reactjs.org"
                        target="_blank"
                        rel="noopener noreferrer"
                    >
                        Learn React
                    </a>
                </header>
            </div>
        );
>>>>>>> Stashed changes
  }
}

export default App;
