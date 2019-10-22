import { combineReducers } from 'redux';
import emfModelReducer from './emfModel';
import dslProcessDenfinitions from './dslProcessDefinitions';
import graphicalEditorReducer from './graphicalEditor';
import toolbarReducer from './toolbar';
import sideToolbarReducer from './sideToolbar';
import modalReducer from './modal';
import propsEditorReducer from './propsEditor'

const rootReducer = combineReducers({
    emfModel: emfModelReducer,
    dslProcessDenfinitions: dslProcessDenfinitions,
    graphicalEditor: graphicalEditorReducer,
    toolbar: toolbarReducer,
    sideToolbar: sideToolbarReducer,
    modal: modalReducer,
    propsEditor: propsEditorReducer
});

export default rootReducer;
