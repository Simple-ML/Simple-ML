import EmfModelVerificationHelper from '../../helper/EmfModelVerificationHelper';
import GenericDataSet from './GenericDataSet';
import GenericDataSetSideBar from './GenericDataSetSideBar';

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
        height: 32, 
        width: 40,
        component: GenericDataSet
    },
    propsEditorMetadata: {
        component: GenericDataSetSideBar
    }
};

export default metadata;