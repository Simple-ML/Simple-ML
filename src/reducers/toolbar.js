
// State initialisation
const initialState = {
    context: {},
    visible: false,
    posX: 0,
    posY: 0
};


// Constants
export const OPEN_TOOLBAR = 'OPEN_TOOLBAR';
export const CLOSE_TOOLBAR = 'CLOSE_TOOLBAR';


// Actions
export const openToolbar = (context, posX, posY) => {
    return {
        type: OPEN_TOOLBAR,
        payload: { context, posX, posY }
    }
};

export const closeToolbar = () => {
    return {
        type: CLOSE_TOOLBAR,
        visible: false
    }
}


// Reducer
export default (state = initialState, action) =>{
    switch(action.type){
        case OPEN_TOOLBAR:
            return Object.assign({}, state, {
                context: action.payload.context,
                posX: action.payload.posX,
                posY: action.payload.posY,
                visible: true
            });
        case CLOSE_TOOLBAR:
            return Object.assign({}, state, initialState);
        default:
            return state;
    }
}
