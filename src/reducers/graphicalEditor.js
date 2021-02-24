import { mxConstants } from "mxgraph-js";

// State initialisation
const initialState = {
    viewMode: mxConstants.DIRECTION_NORTH,
    entityHoveredOver: {}
};


// Constants
const GRAPH_CHANGE_DIRECTION = 'GRAPH_CHANGE_DIRECTION';
const GRAPH_HOVERED_OVER_ENTITY = 'GRAPH_HOVERED_OVER_ENTITY';

// Actions
export const changeDirection = () => {
    return {
        type: GRAPH_CHANGE_DIRECTION
    }
};

export const entityHoverStateEnter = (entity) => {
    return {
        type: GRAPH_HOVERED_OVER_ENTITY,
        payload: {
            entity
        }
    }  
};


// Reducer
const reducer = (state = initialState, action) =>{
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
        case GRAPH_HOVERED_OVER_ENTITY:
            return Object.assign({}, state, {
                entityHoveredOver: action.payload.entity
            });
        default:
            return state;
    }
}
export default reducer;