import EmfModelVerificationHelper from '../../helper/EmfModelVerificationHelper';
import ProcessCallLoadDataSet from './ProcessCallLoadDataSet';

const metadata = {
    verify: (rawEmfEntity) => {
        return EmfModelVerificationHelper.verifyRawEmfEntity(rawEmfEntity, {
            className: EmfModelVerificationHelper.CONSTANTS.SmlCall,
            receiver: {
                className: EmfModelVerificationHelper.CONSTANTS.SmlReference,
                declaration: {
                    className: EmfModelVerificationHelper.CONSTANTS.SmlFunction,
                    // TODO: resolve name from UBO-Api
                    $ref: 'file:/Users/albertdanewitz/xtext-workspace/DSL/bootstrap/de.unibonn.simpleml.parent/de.unibonn.simpleml/target/classes/simpleml/dataset/dataset.stub.simpleml#//@members.1'
                }
            }
        });
    },
    mxGraphMetadata: {
        component: ProcessCallLoadDataSet,
    },
    propsEditorComponent: {} // TODO
}

export default metadata;