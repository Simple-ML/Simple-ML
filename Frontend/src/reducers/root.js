import { combineReducers } from 'redux';
import emfModelReducer from './emfModel';
import graphicalEditorReducer from './graphicalEditor';
import contextMenuReducer from './contextMenu';
import ToolbarReducer from './toolbar';
import modalReducer from './modal';
import runtimeReducer from './runtime';

const rootReducer = combineReducers({
    emfModel: emfModelReducer,
    graphicalEditor: graphicalEditorReducer,
    contextMenu: contextMenuReducer,
    toolbar: ToolbarReducer,
    modal: modalReducer,
    runtime: runtimeReducer
});

export default rootReducer;
