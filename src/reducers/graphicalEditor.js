import { mxConstants } from "mxgraph-js";
import EmfModelHelper from "../helper/EmfModelHelper";


const initialState = {
    viewMode: mxConstants.DIRECTION_WEST,
    emfModel: {},
    emfModelFlat: {}
};

export default (state = initialState, action) =>{
    switch(action.type){
        case 'NEW_EMF_MODEL':
            return Object.assign({}, state, {
                emfModel: action.payload,
                emfModelFlat: EmfModelHelper.flattenEmfModelTree(JSON.parse(action.payload))
            });
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
