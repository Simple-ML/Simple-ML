import EmfModelVerificationHelper from '../../helper/EmfModelVerificationHelper';
import GenericProcessCall from './GenericProcessCall';

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
    propsEditorComponent: {} // TODO
}

export default metadata;