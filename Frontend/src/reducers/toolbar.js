// State initialisation
const initialState = {
    visible: false,
};


// Constants
const SHOW_TOOLBAR = 'SHOW_TOOLBAR';
const HIDE_TOOLBAR = 'HIDE_TOOLBAR';


// Actions
export const showToolbar = () => {
    return {
        type: SHOW_TOOLBAR
    }
};

export const hideToolbar = () => {
    return {
        type: HIDE_TOOLBAR
    }
};


// Reducer
const reducer = (state = initialState, action) => {
    switch(action.type) {
        case SHOW_TOOLBAR:
            return Object.assign({}, state, {
                visible: true
            });
        case HIDE_TOOLBAR:
            return Object.assign({}, state, {
                visible: false
            });
        default:
            return state
    }
}
export default reducer;