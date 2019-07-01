import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';

import TextEditorWrapper from './components/EditorView/TextEditor/TextEditorWrapper';

window.loadEditor((xtextEditor) => {

    TextEditorWrapper.create(xtextEditor);
    /*TextEditorWrapper.setText('\n' +
        'dataframe1 = read_tsv("data/data.tsv")\n' +
        'dataframe2 = read_tsv("data/data2.tsv")\n' +
        'x = project(dataframe1, ["dow"])\n' +
        'y = project(dataframe1, dataframe2, x)\n' +
        '\n' +
        '$UNCONNECTED NODES (only relevant for graphical DSL)$\n\n' +
        'TIME 01:03:05\n' +
        'read_tsv("something")');*/
    TextEditorWrapper.setText('\n' +
    'dataframe1 = read_tsv("data/data.tsv")\n')
    window.loadEditor = undefined;

    ReactDOM.render(<App/>, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
    serviceWorker.unregister();
})
