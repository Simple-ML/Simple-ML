import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';

import TextEditorWrapper from './components/EditorView/TextEditor/TextEditorWrapper';

import afterReactInit from './debugging/afterReactInit';
import exposeToBrowserConsole from "./debugging/exposeToBrowserConsole";

window.loadEditor((xtextEditor) => {

    TextEditorWrapper.create(xtextEditor);
    TextEditorWrapper.setText('\n' +
        'dataframe1 = read_tsv("data/data.tsv")\n' +
        'x = project(dataframe1, ["dow"])\n' +
        '\n' +
        '$UNCONNECTED NODES (only relevant for graphical DSL)$\n' +
        '\n' +
        'read_tsv("something")');

    window.loadEditor = undefined;

    afterReactInit();
    exposeToBrowserConsole();

    ReactDOM.render(<App/>, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
    serviceWorker.unregister();
})
