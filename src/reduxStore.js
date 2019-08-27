import { createStore } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension'

import rootReducer from './reducers/root';

const reduxStore = createStore(rootReducer, composeWithDevTools());

export default reduxStore;
