import { combineReducers } from 'redux';
import emfModelReducer from './emfModel';
import graphicalEditorReducer from './graphicalEditor';
import toolbarReducer from './toolbar';

const rootReducer = combineReducers({
    emfModel: emfModelReducer,
    graphicalEditor: graphicalEditorReducer,
    toolbar: toolbarReducer
});

export default rootReducer;
