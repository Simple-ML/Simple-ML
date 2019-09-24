
import InferenceCreator from './../../core/Toolbar/InferenceCreator';
import EmfModelHelper from '../../../helper/EmfModelHelper';
import XtextServices from '../../../serverConnection/XtextServices';

import store from '../../../reduxStore';
//icons
import editIcon from '../../../images/contextToolbar/Edit.svg';
import deleteIcon from '../../../images/contextToolbar/Delete.svg';


const disabledBecauseEditingNotAllowed = () => {
    let { emfModel } = store.getState();
    return emfModel.dirty;
}

// -----------------------------------------------------------------------
// edit Emf-Entity inferred form mxCell->Emf-Model

const validationMxCellAndEmfModelLink = (context) => {
    if(context.vertex !== true)
        return false;

    if(context.value.data.className !== undefined)
        return true;

    return true;
}

const openDialogForEditingEmfEntity = (context) => {

}

const editEmfEntityMetaData = {
    text: 'Edit',
    icon: editIcon,
    disabled: disabledBecauseEditingNotAllowed
}

InferenceCreator.addInference(validationMxCellAndEmfModelLink, openDialogForEditingEmfEntity, editEmfEntityMetaData);

// edit Emf-Entity inferred form mxCell->Emf-Model
// -----------------------------------------------------------------------
// deletion of Emf-Entity inferred from mxCell

const validationMxCellVertex = (context) => {
    if(context.vertex !== true)
        return false;
    return true;
};

const deletionMxCellVertex = (context) => {
    let entityPath = EmfModelHelper.getFullHierarchy2(context.value);
    XtextServices.deleteEntity(entityPath);
};

const deletionMxCellVertexMetaData = {
    text: 'Delete',
    icon: deleteIcon,
    disabled: disabledBecauseEditingNotAllowed
};

InferenceCreator.addInference(validationMxCellVertex, deletionMxCellVertex, deletionMxCellVertexMetaData);

// deletion of Emf-Entity inferred from mxCell.vertex
// -----------------------------------------------------------------------
// deletion of association inferred from mxCell.edge

const validationMxCellEdge = (context) => {
    if(context.edge !== true)
        return false;
    return true;
};

const deletionMxCellEdge = (context) => {
    let sourceEntity = context.source.value;
    let targetEntity = context.target.value;
    let sourceEntityPath = EmfModelHelper.getFullHierarchy2(sourceEntity);
    let targetEntityPath = EmfModelHelper.getFullHierarchy2(targetEntity);
    XtextServices.deleteAssociation(sourceEntityPath, targetEntityPath);
};

const deletionMxCellEdgeMetaData = {
    text: 'Delete',
    icon: deleteIcon
};

InferenceCreator.addInference(validationMxCellEdge, deletionMxCellEdge, deletionMxCellEdgeMetaData);

// deletion of association inferred from mxCell.edge
// -----------------------------------------------------------------------
