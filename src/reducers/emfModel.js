import EmfModelHelper from "../helper/EmfModelHelper";

// State initialisation
const initialState = {
    emfModel: [],
    emfModelFlat: []
};


// Constants
export const NEW_EMF_MODEL = 'NEW_EMF_MODEL';


// Actions
export const setNewEmfModel = (newEmfModel) => {
    return {
        type: NEW_EMF_MODEL,
        payload: newEmfModel
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
        default:
            return state;
    }
}
