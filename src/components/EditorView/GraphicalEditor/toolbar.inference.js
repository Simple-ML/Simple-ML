

import InferenceCreator from './../../core/Toolbar/InferenceCreator';
import EmfModelHelper from "../../../helper/EmfModelHelper";
import XtextServices from "../../../serverConnection/XtextServices";

// -----------------------------------------------------------------------
// deletion of Emf-Entity inferred from mxCell

const validationMxCellVertex = (context) => {
    if(context === undefined)
        return false;

    if(context.vertex === true)
        return true;
};

const deletionMxCellVertex = (context) => {
    let entityPath = EmfModelHelper.getFullHierarchy2(context.value);
    XtextServices.deleteEntity(entityPath);
};

const deletionMxCellVertexMetaData = {
    text: 'Delete'
};

InferenceCreator.addInference(validationMxCellVertex, deletionMxCellVertex, deletionMxCellVertexMetaData);

// deletion of Emf-Entity inferred from mxCell.vertex
// -----------------------------------------------------------------------
// deletion of association inferred from mxCell.edge

const validationMxCellEdge = (context) => {
    if(context === undefined)
        return false;

    if(context.edge === true)
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
    text: 'Delete'
};

InferenceCreator.addInference(validationMxCellEdge, deletionMxCellEdge, deletionMxCellEdgeMetaData);

// deletion of association inferred from mxCell.edge
// -----------------------------------------------------------------------
