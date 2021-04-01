
import React from 'react'

const DefaultModal = ({ closeModal, context }) => {
    const title = 'test';
    const message = '';

    return (
        <div>
            <h2>Hello</h2>
                <button onClick={closeModal}>close</button>
                <div>{title}</div>
                <div>{message}</div>
                <form>
                    <input />
                    <button>tab navigation</button>
                    <button>stays</button>
                    <button>inside</button>
                    <button>the modal</button>
                </form>
        </div>
    )
}

export default DefaultModal;

