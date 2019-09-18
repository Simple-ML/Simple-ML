import { combineReducers } from 'redux';
import emfModelReducer from './emfModel';
import graphicalEditorReducer from './graphicalEditor';
import toolbarReducer from './toolbar';
import sideToolbar from "./sideToolbar";

const rootReducer = combineReducers({
    emfModel: emfModelReducer,
    graphicalEditor: graphicalEditorReducer,
    toolbar: toolbarReducer,
    sideToolbar: sideToolbar
});

export default rootReducer;
