import EmfModelVerificationHelper from '../../helper/EmfModelVerificationHelper';
import GenericProcessCall from './GenericProcessCall';
import GenericProcessCallSideBar from './GenericProcessCallSideBar';

const metadata = {
    verify: (rawEmfEntity) => {
        return EmfModelVerificationHelper.verifyRawEmfEntity(rawEmfEntity, {
            className: EmfModelVerificationHelper.CONSTANTS.SmlCall
        });
    },
    mxGraphMetadata: {
        height: 48, 
        width: 48,
        component: GenericProcessCall,
    },
    propsEditorMetadata: {
        component: GenericProcessCallSideBar
    }
}

export default metadata;