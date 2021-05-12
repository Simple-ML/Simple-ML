import React from 'react';
import Modal from 'react-modal';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { closeModal } from '../../../reducers/modal';

import containerStyle from './ModalContainer.module.scss';

// Make sure to bind modal to your appElement (http://reactcommunity.org/react-modal/accessibility/)
Modal.setAppElement(document.getElementById('root'));

class ModalContainer extends React.Component {
    constructor(props) {
        super(props);

        this.closeModal = this.closeModal.bind(this);
    }

    closeModal() {
        this.props.closeModal();
    }

    render() {
        if(this.props.modalBody === undefined)
            return null;

        let ModalBody = this.props.modalBody;
        return (
            <div>
                <Modal
                    isOpen={this.props.modalIsOpen}
                    onRequestClose={this.closeModal}
                    overlayClassName={containerStyle["Modal-overlay"]}
                    className={this.props.wide ? containerStyle['Modal-content-wide'] : containerStyle["Modal-content"]}
                    contentLabel="Example Modal"
                >
                    <ModalBody
                        closeModal={this.closeModal}
                        context={this.props.modalContext}
                    />
                </Modal>
            </div>
        );
    }
}

ModalContainer.propTypes = {
    modalContext: PropTypes.object.isRequired,
    modalIsOpen: PropTypes.bool.isRequired,

    closeModal: PropTypes.func.isRequired
};

const mapStateToProps = state => {
    return {
        modalBody: state.modal.body,
        modalContext: state.modal.context,
        modalIsOpen: state.modal.isOpen,
        wide: state.modal.wide
    }
};

const mapDispatchToProps = dispatch => {
    return {
        closeModal: () => dispatch(closeModal())
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ModalContainer);

