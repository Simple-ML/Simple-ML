import { mxConstants } from "mxgraph-js";


const initialState = {
    viewMode: mxConstants.DIRECTION_WEST
};

export default (state = initialState, action) =>{
    switch(action.type){
        case 'MX_GRAPH_CHANGE_ALIGNMENT':
            switch(state.viewMode) {
                case mxConstants.DIRECTION_WEST:
                    return Object.assign({}, state, {
                        viewMode: mxConstants.DIRECTION_NORTH
                    });
                case mxConstants.DIRECTION_NORTH:
                    return Object.assign({}, state, {
                        viewMode: mxConstants.DIRECTION_WEST
                    });
                default:
                    return state;
            }
        default:
            return state;
    }
}
