import { combineReducers } from 'redux';
import emfModelReducer from './emfModel';
import graphicalEditorReducer from './graphicalEditor';

const rootReducer = combineReducers({
    emfModel: emfModelReducer,
    graphicalEditor: graphicalEditorReducer
});

export default rootReducer;
