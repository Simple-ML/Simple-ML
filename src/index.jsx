import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import store from './reduxStore';

import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';

import TextEditorWrapper from './components/EditorView/TextEditor/TextEditorWrapper';

import afterReactInit from './debugging/afterReactInit';
import { exposeToBrowserConsole } from "./debugging/exposeToBrowserConsole";
import XtextServices from "./serverConnection/XtextServices";
import { setNewEmfModel, setEmfModelClean, setEmfModelDirty } from "./reducers/emfModel";

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
            case 'validate':
                // TODO: not the right place for this code (containsError)
                const containsError = (issues) => {
                    return 0 < issues.filter((item) => {
                        if(item.severity === 'error')
                            return true;
                        else
                            return false;
                    }).length
                };

                if(containsError(result.issues))
                    store.dispatch(setEmfModelDirty());
                else
                    store.dispatch(setEmfModelClean());

                XtextServices.getEmfModel();
                break;
            default:
                break;
        }
    });

    ReactDOM.render(
        <Provider store={store}>
            <App/>
        </Provider>,
        document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
    serviceWorker.unregister();

    afterReactInit();
    exposeToBrowserConsole();
});
