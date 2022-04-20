
import InferenceCreator from './../../core/ContextMenu/InferenceCreator';
import EmfModelHelper from '../../../helper/EmfModelHelper';
import genericDataSetMetadata from '../../../emfMetadata/GenericDataSet/GenericDataSet.metadata';
import XtextServices from '../../../serverConnection/XtextServices';

import store from '../../../reduxStore';
import { showModal } from '../../../reducers/modal';

import DataSetModal from '../../core/Modal/DataSetModal';

//icons
import editIcon from '../../../images/contextToolbar/Edit.svg';
import deleteIcon from '../../../images/contextToolbar/Delete.svg';


const disabledBecauseEditingNotAllowed = () => {
    let { emfModel } = store.getState();
    return emfModel.dirty;
}

// -----------------------------------------------------------------------
// show dataset inferred form mxCell->Emf-Model

const validationMxCellAsEmfDataSetAndPlaceholderFilled = (context) => {
    if(context.vertex !== true)
        return false;
    
    if(!genericDataSetMetadata.verify(context.emfReference.raw))
        return false;

    if(store.getState().runtime.placeholder[context.emfReference.data.name] === undefined)
        return false;

    return true;
}

const openModalWithDataSetValue = (context) => {
    store.dispatch(showModal(DataSetModal, context, true));
}

const editEmfEntityMetaData = {
    text: 'View',
    icon: editIcon,
    disabled: disabledBecauseEditingNotAllowed
}

InferenceCreator.addInference(validationMxCellAsEmfDataSetAndPlaceholderFilled, openModalWithDataSetValue, editEmfEntityMetaData);

// show dataset inferred form mxCell->Emf-Model
// -----------------------------------------------------------------------
// execute code inferred from mxCell

const validationMxCellVertex = (context) => {
    if(context.vertex !== true)
        return false;
    return true;
};

const deletionMxCellVertex = (context) => {
    XtextServices.generate();
};

const deletionMxCellVertexMetaData = {
    text: 'Generate',
    icon: deleteIcon,
    needInputData: false,
    disabled: disabledBecauseEditingNotAllowed
};

InferenceCreator.addInference(validationMxCellVertex, deletionMxCellVertex, deletionMxCellVertexMetaData);

// execute code inferred from mxCell.vertex
// -----------------------------------------------------------------------