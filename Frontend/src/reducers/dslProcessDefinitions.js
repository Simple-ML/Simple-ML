
// State initialisation
const initialState = {
    processDefinitions: [],
};


// Constants
const SET_DSL_PROCESS_DEFINITIONS = 'SET_DSL_PROCESS_DEFINITIONS';


// Actions
export const setDslProcessDefinitions = (processDefinitions) => {
    return {
        type: SET_DSL_PROCESS_DEFINITIONS,
        processDefinitions: processDefinitions
    }
};


// Reducer
export default (state = initialState, action) =>{
    switch(action.type){
        case SET_DSL_PROCESS_DEFINITIONS:
            return Object.assign({}, state, {
                processDefinitions: action.processDefinitions
            });
        default:
            return state;
    }
}
