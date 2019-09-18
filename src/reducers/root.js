import { combineReducers } from 'redux';
import emfModelReducer from './emfModel';
import graphicalEditorReducer from './graphicalEditor';
import sideToolbar from "./sideToolbar";

const rootReducer = combineReducers({
    emfModel: emfModelReducer,
    graphicalEditor: graphicalEditorReducer,
    sideToolbar: sideToolbar
});

export default rootReducer;
