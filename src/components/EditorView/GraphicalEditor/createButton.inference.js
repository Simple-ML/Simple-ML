import XtextServices from '../../../serverConnection/XtextServices';
import InferenceCreator from '../../core/ContextMenu/InferenceCreator';

import { showModal } from '../../../reducers/modal';
import DefaultModal from '../../core/Modal/DefaultModal';
import store from "../../../reduxStore";

export const createButtonVerificationToken = {
    part1: 'yes, im the create-button',
    part2: 'here some random number',
    part3: '86138783161577'
}

const verifyCreateButton = (context) => {
    if (context.part1 !== createButtonVerificationToken.part1)
        return false;
    if (context.part2 !== createButtonVerificationToken.part2)
        return false;
    if (context.part3 !== createButtonVerificationToken.part3)
        return false;

    return true;
}

const createDefaultAssignment = (context) => {
    XtextServices.createEntity({

    });
}

InferenceCreator.addInference(verifyCreateButton, createDefaultAssignment, {text: 'create Assignment'});

const createDefaultProcess = (context) => {
    XtextServices.createEntity({

    });
}

InferenceCreator.addInference(verifyCreateButton, createDefaultProcess, {text: 'create Process'});

const openCreateModal = (context) => {
    store.dispatch(showModal(DefaultModal, {
        title: 'create entity',
        message: 'here you can create a entity (someday)'
    }));
}

InferenceCreator.addInference(verifyCreateButton, openCreateModal, {text: 'create ...'});
