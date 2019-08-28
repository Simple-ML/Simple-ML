import React from 'react';

import store from '../reduxStore';

class ReactReduxComponent extends React.Component {

    unsubscribeStore = () => {};

    constructor(props, reduxCallback = () => {}) {
        super(props)
        this.state = {};
        this.unsubscribeStore = store.subscribe(() => {
            reduxCallback();
        });
    }

    updateReduxCallback = (newReduxCallback) => {
        this.unsubscribeStore();
        this.unsubscribeStore = store.subscribe(() => {
            newReduxCallback();
        })
    }

    dispatchReduxAction = (action) => {
        store.dispatch(action);
    }
}

export default ReactReduxComponent;
