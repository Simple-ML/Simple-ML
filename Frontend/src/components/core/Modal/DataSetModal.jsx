import React from 'react';
import { connect } from 'react-redux';

import { closeModal } from '../../../reducers/modal';

class DataSetModal extends React.Component{

    getPlaceholder = () => {
        return this.props.placeholder[this.props.context.emfReference.data.name];
    }

    render() {
        const placeholderValue = this.getPlaceholder();
        console.log(placeholderValue)
        return (
            <div>
					{JSON.stringify(placeholderValue)} <br/>
                <button onClick={this.props.closeModal}>close</button>
            </div>
        )
    }
}


const mapStateToProps = state => {
    return {
        placeholder: state.runtime.placeholder
    }
};

const mapDispatchToProps = dispatch => {
    return {
        closeModal: () => dispatch(closeModal())
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(DataSetModal);
