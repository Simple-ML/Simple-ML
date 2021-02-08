import EmfModelVerificationHelper from '../../helper/EmfModelVerificationHelper';
import GenericDataSet from './GenericDataSet';

export default {
    verify: (rawEmfEntity) => {
        if (!EmfModelVerificationHelper.verifyRawEmfEntity(rawEmfEntity, {
            className: EmfModelVerificationHelper.CONSTANTS.SmlPlaceholder
        })) {
            return false;
        }
        if (rawEmfEntity['$ref'] !== undefined)
            return false;

        return true;
    },
    mxGraphMetadata: {
        component: GenericDataSet,
    },
    propsEditorComponent: {} // TODO
}