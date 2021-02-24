import EmfModelVerificationHelper from '../../helper/EmfModelVerificationHelper';
import GenericDataSet from './GenericDataSet';

const metadata = {
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
        height: 38, 
        width: 48,
        component: GenericDataSet
    },
    propsEditorComponent: {} // TODO
};
export default metadata;