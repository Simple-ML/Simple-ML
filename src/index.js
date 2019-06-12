import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';

import TextEditorWrapper from './Components/TextEditor/TextEditorWrapper';

window.loadEditor((xtextEditor) => {

    TextEditorWrapper.create(xtextEditor);
    TextEditorWrapper.setText("\n" +
        "source db1\n" +
        "source db2\n" +
        "source server12\n" +
        "\n" +
        "method ml1\n" +
        "method ml2\n" +
        "\n" +
        "collection someCollection db1[12] \"some text\" 987\n" +
        "\n" +
        "collection collection2 db2 \"some text\" 334444455\n" +
        "\n" +
        "function combine input db1 server12 ml-method ml1\n");

    window.loadEditor = undefined;

    ReactDOM.render(<App/>, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
    serviceWorker.unregister();
})
