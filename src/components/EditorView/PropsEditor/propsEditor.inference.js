import XtextServices from '../../../serverConnection/XtextServices';
import InferenceCreator from '../../core/Toolbar/InferenceCreator';

import { showModal } from '../../../reducers/modal';
import DefaultModal from '../../core/Modal/DefaultModal';
import store from "../../../reduxStore";

export const confirmEditButtonVerificationToken = {
    part1: 'yes, im the edit-button',
    part2: 'here some random number',
    part3: '1234567890'
}

const verifyConfirmEditButton = (context) => {
    if (context.part1 !== createButtonVerificationToken.part1)
        return false;
    if (context.part2 !== createButtonVerificationToken.part2)
        return false;
    if (context.part3 !== createButtonVerificationToken.part3)
        return false;

    return true;
}

const updateEntity = (context) => {
    /*XtextServices.createEntity({

    });*/
    console.log("XtextServices.updateEntity({context})")
}

InferenceCreator.addInference(verifyConfirmEditButton, updateEntity, {text: 'update entity'});
