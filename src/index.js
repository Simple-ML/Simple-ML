import React from 'react';
import ReactDOM from 'react-dom';
import store from './reduxStore';

import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';

import TextEditorWrapper from './components/EditorView/TextEditor/TextEditorWrapper';

import afterReactInit from './debugging/afterReactInit';
import { exposeToBrowserConsole } from "./debugging/exposeToBrowserConsole";
import XtextServices from "./serverConnection/XtextServices";
import { setNewEmfModel } from "./reducers/emfModel";

window.loadEditor((xtextEditor) => {
    window.loadEditor = undefined;
    TextEditorWrapper.create(xtextEditor);

    XtextServices.addSuccessListener((serviceType, result) => {
        switch (serviceType) {
            case 'getEmfModel':
            case 'deleteEntity':
            case 'deleteAssociation':
            case 'createAssociation':
                store.dispatch(setNewEmfModel(result.emfModel));
                break;
            default:
                break;
        }
    });

    ReactDOM.render(
        <App/>,
        document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
    serviceWorker.unregister();

    afterReactInit();
    exposeToBrowserConsole();
});
