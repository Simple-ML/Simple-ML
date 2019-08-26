import { createStore, combineReducers } from 'redux';
import graphicalEditorReducer from './reducers/graphicalEditor';
import { composeWithDevTools } from 'redux-devtools-extension'

const rootReducer = combineReducers({
    mxGraph: graphicalEditorReducer
});

const reduxStore = createStore(rootReducer, composeWithDevTools());

export default reduxStore;
