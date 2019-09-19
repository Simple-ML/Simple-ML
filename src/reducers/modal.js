// State initialisation
const initialState = {
    body: undefined,
    wide: false,
    context: {},
    isOpen: false
}


// Constants
const SHOW_MODAL = 'SHOW_MODAL';
const CLOSE_MODAL = 'CLOSE_MODAL';


// Actions
export const showModal = (bodyComponent, context, wide = false) => {
    return {
        type: SHOW_MODAL,
        modalBody: bodyComponent,
        modalWide: wide,
        modalContext: context
    }
}

export const closeModal = () => {
    return {
        type: CLOSE_MODAL
    }
}


// Reducer
export default (state = initialState, action) => {
    switch (action.type) {
        case SHOW_MODAL:
            return Object.assign({}, state, {
                body: action.modalBody,
                wide: action.modalWide,
                context: action.modalContext,
                isOpen: true
            });
        case CLOSE_MODAL:
            return Object.assign({}, state, initialState);
        default:
            return state;
    }
}
