import store from '../reduxStore';
import XtextServices from './XtextServices';
import RuntimeServices from './RuntimeService';
import EmfModelHelper from '../helper/EmfModelHelper';
import { setNewEmfModel, setEmfModelClean, setEmfModelDirty, setNewProcessMetadata } from '../reducers/emfModel';
import { setDslProcessDefinitions } from '../reducers/dslProcessDefinitions';
import { deleteAllPlaceholder } from '../reducers/runtime';

const defaultListeners = () => {
    XtextServices.addSuccessListener((serviceType, result) => {
        switch (serviceType) {
            case 'getEmfModel':
                // let emfRaw = JSON.parse(result.emfModel);
                // let emfFlat = EmfModelHelper.flattenEmfModelTree(emfRaw);
                // let emfRenderable = EmfModelHelper.getRenderableEmfEntities(emfFlat);
                // store.dispatch(setNewEmfModel(emfRaw, emfFlat, emfRenderable));
                // break;
            case 'createEntity':
            case 'deleteEntity':
            case 'deleteAssociation':
            case 'createAssociation':
                const emfRaw = JSON.parse(result.emfModel);
                const emfFlat = EmfModelHelper.flattenEmfModelTree(emfRaw);
                const emfRenderable = EmfModelHelper.getRenderableEmfEntities(emfFlat);
                const emfAssosiatins = EmfModelHelper.getEmfEntityAssociations(emfFlat);
                store.dispatch(setNewEmfModel(emfRaw, emfFlat, emfRenderable, emfAssosiatins));

                const emfPathCollection = EmfModelHelper.getProcessEmfPathFromFlatEntities(emfFlat);

                XtextServices.getProcessMetadata(emfPathCollection);

                // TODO: update text-editor XtextServices.validate does not work
                XtextServices.validate();
                break;
            case 'getProcessMetadata':
                store.dispatch(setNewProcessMetadata(JSON.parse(result.info)));
                break;
            case 'validate':
                // TODO: not the right place for this code (containsError)
                const containsError = (issues) => {
                    return 0 < issues.filter((item) => {
                        if(item.severity === 'error')
                            return true;
                        else
                            return false;
                    }).length
                };

                if(containsError(result.issues))
                    store.dispatch(setEmfModelDirty());
                else
                    store.dispatch(setEmfModelClean());

                XtextServices.getEmfModel();
                break;
            case 'getProcessProposals':
                store.dispatch(setDslProcessDefinitions(JSON.parse(result.fullText)));
                break;
            case 'generate':
                store.dispatch(deleteAllPlaceholder());
                RuntimeServices.runWorkflow(result.artifacts);
                break;
            default:
                break;
        }
    });
}
export default defaultListeners;