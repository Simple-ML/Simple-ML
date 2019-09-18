// State initialisation
const initialState = {
    visible: false,
};


// Constants
const SHOW_SIDE_TOOLBAR = 'SHOW_SIDE_TOOLBAR';
const HIDE_SIDE_TOOLBAR = 'HIDE_SIDE_TOOLBAR';


// Actions
export const showSideToolbar = () => {
    return {
        type: SHOW_SIDE_TOOLBAR
    }
};

export const hideSideToolbar = () => {
    return {
        type: HIDE_SIDE_TOOLBAR
    }
};


// Reducer
export default (state = initialState, action) => {
    switch(action.type) {
        case SHOW_SIDE_TOOLBAR:
            return Object.assign({}, state, {
                visible: true
            });
        case HIDE_SIDE_TOOLBAR:
            return Object.assign({}, state, {
                visible: false
            });
        default:
            return state
    }
}
