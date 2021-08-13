
// State initialisation
const initialState = {
    context: {},
    visible: false,
    posX: 0,
    posY: 0
};


// Constants
export const OPEN_CONTEXT_MENU = 'OPEN_CONTEXT_MENU';
export const CLOSE_CONTEXT_MENU = 'CLOSE_CONTEXT_MENU';


// Actions
export const openContextMenu = (context, posX, posY) => {
    return {
        type: OPEN_CONTEXT_MENU,
        payload: { context, posX, posY }
    }
};

export const closeContextMenu = () => {
    return {
        type: CLOSE_CONTEXT_MENU,
        visible: false
    }
}


// Reducer
const reducer = (state = initialState, action) =>{
    switch(action.type){
        case OPEN_CONTEXT_MENU:
            return Object.assign({}, state, {
                context: action.payload.context,
                posX: action.payload.posX,
                posY: action.payload.posY,
                visible: true
            });
        case CLOSE_CONTEXT_MENU:
            return Object.assign({}, state, initialState);
        default:
            return state;
    }
}
export default reducer;