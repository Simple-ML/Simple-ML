import React from 'react';
import store from '../../reduxStore';

export default class GenericDataSetSideBar extends React.Component {

    constructor(props) {
        super(props);

        this.onStoreChange = this.onStoreChange.bind(this);

        this.unsubscribe = store.subscribe(() => {
            this.setState(this.onStoreChange(store.getState()));
        });

        this.state = this.onStoreChange(store.getState());
    }

    onStoreChange = (state) => {
        const placeholderName = this.props.entity.data ? this.props.entity.data.name : ' '
        return {
            placeholderValue: state.runtime.placeholder[placeholderName]
        };
    }

    componentWillUnmount() {
        this.unsubscribe();
    }

    render() {
        if(this.props.entity.data) {
            return (
                <div>
                    <div>Name</div>
                    <div>{this.props.entity.data.name}</div>
                    <div> </div>
                    <div>Value</div>
                    <div>{this.state.placeholderValue}</div>
                </div>    
            )
        } else {
            return (<div></div>)
        }
    }
}