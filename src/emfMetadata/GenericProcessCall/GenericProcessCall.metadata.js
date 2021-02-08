import EmfModelVerificationHelper from '../../helper/EmfModelVerificationHelper';
import GenericProcessCall from './GenericProcessCall';

export default {
    verify: (rawEmfEntity) => {
        return EmfModelVerificationHelper.verifyRawEmfEntity(rawEmfEntity, {
            className: EmfModelVerificationHelper.CONSTANTS.SmlCall
        });
    },
    mxGraphMetadata: {
        component: GenericProcessCall,
    },
    propsEditorComponent: {} // TODO
}