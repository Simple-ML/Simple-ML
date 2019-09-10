import { mxConstants } from "mxgraph-js";

// State initialisation
const initialState = {
    viewMode: mxConstants.DIRECTION_WEST,
};


// Constants
const GRAPH_CHANGE_DIRECTION = 'GRAPH_CHANGE_DIRECTION';


// Actions
export const changeDirection = () => {
    return {
        type: GRAPH_CHANGE_DIRECTION
    }
};


// Reducer
export default (state = initialState, action) =>{
    switch(action.type){
        case GRAPH_CHANGE_DIRECTION:
            let newDirection = (() => {
                switch(state.viewMode) {
                    case mxConstants.DIRECTION_WEST:
                        return mxConstants.DIRECTION_NORTH;
                    case mxConstants.DIRECTION_NORTH:
                        return mxConstants.DIRECTION_WEST;
                    default:
                        return mxConstants.DIRECTION_NORTH;
                }
            })();
            return Object.assign({}, state, {
                viewMode: newDirection
            });
        default:
            return state;
    }
}
