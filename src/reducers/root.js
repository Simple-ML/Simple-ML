import { combineReducers } from 'redux';
import emfModelReducer from './emfModel';
import dslProcessDefinitions from './dslProcessDefinitions';
import graphicalEditorReducer from './graphicalEditor';
import toolbarReducer from './toolbar';
import sideToolbarReducer from './sideToolbar';
import modalReducer from './modal';
import propsEditorReducer from './propsEditor'

const rootReducer = combineReducers({
    emfModel: emfModelReducer,
    dslProcessDefinitions: dslProcessDefinitions,
    graphicalEditor: graphicalEditorReducer,
    toolbar: toolbarReducer,
    sideToolbar: sideToolbarReducer,
    modal: modalReducer,
    propsEditor: propsEditorReducer
});

export default rootReducer;
