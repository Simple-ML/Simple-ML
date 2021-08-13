// State initialisation
const initialState = {
    raw: [],                // raw Emf-Model-Tree (from Xtext-Server)
    flat: [],               // flat version of the raw Emf-Model-Tree
    renderable: [],         // subcollection fo flat Emf-Model-Tree (only items with associated metadata for rendering purposes)
    associations: [],       // list of parent-child-associations of renderable eintities
    dirty: true
};


// Constants
export const NEW_EMF_MODEL = 'NEW_EMF_MODEL';
export const CLEAN_EMF_MODEL = 'CLEAN_EMF_MODEL';
export const DIRTY_EMF_MODEL = 'DIRTY_EMF_MODEL';

// Actions
export const setNewEmfModel = (raw, flat, renderable, associations) => {
    return {
        type: NEW_EMF_MODEL,
        payload: {
            raw,
            flat,
            renderable,
            associations
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
                renderable: action.payload.renderable,
                associations: action.payload.associations
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
