//node_module
import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
//redux
import InferenceCreator from './InferenceCreator';
import { closeContextMenu } from '../../../reducers/contextMenu';
//style
import ContextMenuStyle from './contextMenu.module.scss';

class ContextMenu extends React.Component {
    constructor(props) {
        super(props);

        this.myself = React.createRef();
    }

    /**
     * Creates default disable-function
     * @param metaData
     */
    prepareMetaData = (metaData) => {
        metaData.forEach((item) => {
            item.metaData.disabled  = item.metaData.disabled !== undefined ?
                item.metaData.disabled : () => { return false; }
        })
    }

    render() {
        /**
         * Expected MetaData-Object:
         * {
         *     metadata: {
         *         icon: object,                        // source or path of image what can be passed to src-parameter in a html-tag
         *         text: string                         // will be displayed
         *         disabled: () => { returns bool }     // indicates if func (below) can be executed
         *     },
         *     func: () => { returns void }             // function to be executed (div->onClick())
         * }
         * @type {Array}
         */
        let buttonMetaData = InferenceCreator.inferFromContext(this.props.context);
        this.prepareMetaData(buttonMetaData);

        let { posX, posY, visible } = this.props;

        visible = visible && buttonMetaData.length > 0;
        visible = visible ? 'visible' : 'hidden';

        return(
            <div style={{visibility: visible}}>
                <div className={ContextMenuStyle.toolbar}
                     style={{top: posY, left: posX}}
                     ref={this.myself}>
                    {
                        buttonMetaData.map((item, i) => {
                            return(
                                <button className={ContextMenuStyle["toolbar-button"]}
                                    key={i}
                                    disabled={item.metaData.disabled()}
                                    onClick={() => {
                                        item.func();
                                        this.props.closeContextMenu();
                                    }
                                }>
                                    <img className={ContextMenuStyle.icon} src={item.metaData.icon}/>
                                    <div>{item.metaData.text}</div>
                                </button>
                            )
                        })
                    }
                </div>
                <div className={ContextMenuStyle["toolbar-outside"]}
                     onClick={this.props.closeContextMenu}>
                </div>
            </div>
        )
    }
}

ContextMenu.propTypes = {
    context: PropTypes.object.isRequired,
    posX: PropTypes.number.isRequired,
    posY: PropTypes.number.isRequired,
    visible: PropTypes.bool.isRequired,
    closeContextMenu: PropTypes.func.isRequired
};

const mapStateToProps = state => {
    return {
        context: state.contextMenu.context,
        posX: state.contextMenu.posX,
        posY: state.contextMenu.posY,
        visible: state.contextMenu.visible
    };
};

const mapDispatchToProps = dispatch => {
    return {
        closeContextMenu: () => dispatch(closeContextMenu())
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(ContextMenu);
