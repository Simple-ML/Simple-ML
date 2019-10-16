
// State initialisation
const initialState = {
    context: {},
    visible: false,
};


// Constants
export const OPEN_PROPS_EDITOR = 'OPEN_PROPS_EDITOR';
export const CLOSE_PROPS_EDITOR = 'CLOSE_PROPS_EDITOR';


// Actions
export const openPropsEditor = (context) => {
    return {
        type: OPEN_PROPS_EDITOR,
        payload: { context }
    }
};

export const closePropsEditor = () => {
    return {
        type: CLOSE_PROPS_EDITOR,
        visible: false
    }
}


// Reducer
export default (state = initialState, action) =>{
    switch(action.type){
        case OPEN_PROPS_EDITOR:
            return Object.assign({}, state, {
                context: action.payload.context,
                visible: true
            });
        case CLOSE_PROPS_EDITOR:
            return Object.assign({}, state, initialState);
        default:
            return state;
    }
}
