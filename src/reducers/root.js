import { combineReducers } from 'redux';
import emfModelReducer from './emfModel';
import graphicalEditorReducer from './graphicalEditor';
import toolbarReducer from './toolbar';
import sideToolbarReducer from './sideToolbar';
import modalReducer from './modal';

const rootReducer = combineReducers({
    emfModel: emfModelReducer,
    graphicalEditor: graphicalEditorReducer,
    toolbar: toolbarReducer,
    sideToolbar: sideToolbarReducer,
    modal: modalReducer
});

export default rootReducer;
