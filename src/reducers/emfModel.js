import EmfModelHelper from "../helper/EmfModelHelper";

// State initialisation
const initialState = {
    raw: [],
    flat: [],
    renderable: [],
    dirty: true
};


// Constants
export const NEW_EMF_MODEL = 'NEW_EMF_MODEL';
export const CLEAN_EMF_MODEL = 'CLEAN_EMF_MODEL';
export const DIRTY_EMF_MODEL = 'DIRTY_EMF_MODEL';

// Actions
export const setNewEmfModel = (raw, flat, renderable) => {
    return {
        type: NEW_EMF_MODEL,
        payload: {
            raw,
            flat,
            renderable
        }
    }
};

export const setEmfModelClean = () => {
    return {
        type: CLEAN_EMF_MODEL
    }
};

export const setEmfModelDirty = () => {
    return {
        type: DIRTY_EMF_MODEL
    }
};


// Reducer
export default (state = initialState, action) =>{
    switch(action.type){
        case NEW_EMF_MODEL:
            return Object.assign({}, state, {
                raw: action.payload.raw,
                flat: action.payload.flat,
                renderable: action.payload.renderable
            });
        case CLEAN_EMF_MODEL:
            return Object.assign({}, state, {
                dirty: false
            });
        case DIRTY_EMF_MODEL:
            return Object.assign({}, state, {
                dirty: true
            });
        default:
            return state;
    }
}
