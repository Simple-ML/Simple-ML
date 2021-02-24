import { combineReducers } from 'redux';
import emfModelReducer from './emfModel';
import dslProcessDefinitions from './dslProcessDefinitions';
import graphicalEditorReducer from './graphicalEditor';
import contextMenuReducer from './contextMenu';
import sideToolbarReducer from './sideToolbar';
import modalReducer from './modal';
import propsEditorReducer from './propsEditor';
import runtimeReducer from './runtime';

const rootReducer = combineReducers({
    emfModel: emfModelReducer,
    dslProcessDefinitions: dslProcessDefinitions,
    graphicalEditor: graphicalEditorReducer,
    contextMenu: contextMenuReducer,
    sideToolbar: sideToolbarReducer,
    modal: modalReducer,
    propsEditor: propsEditorReducer,
    runtime: runtimeReducer
});

export default rootReducer;
