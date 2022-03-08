import store from '../reduxStore';
import XtextServices from './XtextServices';
import RuntimeServices from './RuntimeService';
import EmfModelHelper from '../helper/EmfModelHelper';
import { setNewEmfModel, setEmfModelClean, setEmfModelDirty, setNewProcessMetadata, setNewProcessProposals } from '../reducers/emfModel';
import { deleteAllPlaceholder } from '../reducers/runtime';

const defaultListeners = () => {
    XtextServices.addSuccessListener((serviceType, result) => {
        switch (serviceType) {
            case 'getEmfModel':
                const emfRaw = JSON.parse(result.emfModel);
                const emfFlat = EmfModelHelper.flattenEmfModelTree(emfRaw);
                const emfRenderable = EmfModelHelper.getRenderableEmfEntities(emfFlat);
                const emfAssosiatins = EmfModelHelper.getEmfEntityAssociations(emfFlat);
                store.dispatch(setNewEmfModel(emfRaw, emfFlat, emfRenderable, emfAssosiatins));
                
                const emfPathCollection = EmfModelHelper.getProcessEmfPathFromFlatEntities(store.getState().emfModel.flat);
                XtextServices.getProcessMetadata(emfPathCollection);
                break;
            case 'createEntity':
            case 'deleteEntity':
            case 'deleteAssociation':
            case 'createAssociation':
                XtextServices.update();
                break;
            case 'getProcessMetadata':
                store.dispatch(setNewProcessMetadata(JSON.parse(result.info)));
                break;
            case 'getProcessProposals':
                store.dispatch(setNewProcessProposals(JSON.parse(result.info)));
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

                break;
            case 'update':
                XtextServices.getEmfModel();
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