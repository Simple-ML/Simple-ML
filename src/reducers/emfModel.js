import EmfModelHelper from "../helper/EmfModelHelper";

// State initialisation
const initialState = {
    emfModel: [],
    emfModelFlat: [],
    dirty: true
};


// Constants
export const NEW_EMF_MODEL = 'NEW_EMF_MODEL';
export const CLEAN_EMF_MODEL = 'CLEAN_EMF_MODEL';
export const DIRTY_EMF_MODEL = 'DIRTY_EMF_MODEL';

// Actions
export const setNewEmfModel = (newEmfModel) => {
    return {
        type: NEW_EMF_MODEL,
        payload: newEmfModel
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
                emfModel: action.payload,
                emfModelFlat: EmfModelHelper.flattenEmfModelTree(JSON.parse(action.payload))
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
