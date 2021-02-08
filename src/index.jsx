import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import store from './reduxStore';

import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';

import TextEditorWrapper from './components/EditorView/TextEditor/TextEditorWrapper';

import afterReactInit from './debugging/afterReactInit';
import { exposeToBrowserConsole } from './debugging/exposeToBrowserConsole';
import XtextServices from './serverConnection/XtextServices';
import EmfModelHelper from './helper/EmfModelHelper';
import { setNewEmfModel, setEmfModelClean, setEmfModelDirty } from './reducers/emfModel';
import { setDslProcessDefinitions } from './reducers/dslProcessDefinitions';

window.loadEditor((xtextEditor) => {
    window.loadEditor = undefined;
    TextEditorWrapper.create(xtextEditor);

    XtextServices.addSuccessListener((serviceType, result) => {
        switch (serviceType) {
            case 'getEmfModel':
                // let emfRaw = JSON.parse(result.emfModel);
                // let emfFlat = EmfModelHelper.flattenEmfModelTree(emfRaw);
                // let emfRenderable = EmfModelHelper.getRenderableEmfEntities(emfFlat);
                // store.dispatch(setNewEmfModel(emfRaw, emfFlat, emfRenderable));
                // break;
            case 'createEntity':
            case 'deleteEntity':
            case 'deleteAssociation':
            case 'createAssociation':
                let emfRaw = JSON.parse(result.emfModel);
                let emfFlat = EmfModelHelper.flattenEmfModelTree(emfRaw);
                let emfRenderable = EmfModelHelper.getRenderableEmfEntities(emfFlat);
                let emfAssosiatins = EmfModelHelper.getEmfEntityAssociations(emfFlat);
                store.dispatch(setNewEmfModel(emfRaw, emfFlat, emfRenderable, emfAssosiatins));

                // TODO: update text-editor XtextServices.validate does not work
                XtextServices.validate();
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
                else {
                    store.dispatch(setEmfModelClean());
                    XtextServices.getEmfModel();
                }
                break;
            case 'getProcessProposals':
                store.dispatch(setDslProcessDefinitions(JSON.parse(result.fullText)));
                break;
            case 'generate':
                // TODO: generated Code
                console.log(result);
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
