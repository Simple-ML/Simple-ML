// State initialisation
const initialState = {
    sessionId: '',
    placeholder: {}
};


// Constants
const SET_SESSION_ID = 'SET_SESSION_ID';
const SAVE_PLACEHOLDER_DATA = 'SAVE_PLACEHOLDER_DATA';
const REMOVE_ALL_PLACEHOLDER = 'REMOVE_ALL_PLACEHOLDER';


// Actions
export const setSessionId = (sessionId) => {
    return {
        type: SET_SESSION_ID,
        payload: {
            sessionId
        }
    }
};

export const savePlaceholder = (name, value) => {
    return {
        type: SAVE_PLACEHOLDER_DATA,
        payload: {
            name,
            value
        }
    }
}

export const deleteAllPlaceholder = () => {
    return {
        type: REMOVE_ALL_PLACEHOLDER
    }
}


// Reducer
const reducer = (state = initialState, action) => {
    switch(action.type) {
        case SET_SESSION_ID:
            return Object.assign({}, state, {
                sessionId: action.payload.sessionId
            });
        case SAVE_PLACEHOLDER_DATA:
            const placeholder = state.placeholder;
            placeholder[action.payload.name] = action.payload.value;

            return Object.assign({}, state, {
                placeholder
            });
        case REMOVE_ALL_PLACEHOLDER:
            state.placeholder = {};
            return state;
        default:
            return state
    }
}
export default reducer;