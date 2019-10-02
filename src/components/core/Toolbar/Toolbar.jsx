//node_module
import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
//redux
import InferenceCreator from './InferenceCreator';
import { closeToolbar } from '../../../reducers/toolbar';
//style
import ToolbarStyle from './toolbar.module.scss';

class Toolbar extends React.Component {
    constructor(props) {
        super(props);

        this.myself = React.createRef();
        this.closeToolbar = this.closeToolbar.bind(this);
    }

    closeToolbar = () => {
        this.props.closeToolbar()
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
            visible = visible ? 'visible' : 'hidden';

        return(
            <div style={{visibility: visible}}>
                <div className={ToolbarStyle.toolbar}
                     style={{top: posY, left: posX}}
                     ref={this.myself}>
                    {
                        buttonMetaData.map((item, i) => {
                            return(
                                <button className={ToolbarStyle["toolbar-button"]}
                                    key={i}
                                    disabled={item.metaData.disabled()}
                                    onClick={() => {
                                        item.func();
                                        this.closeToolbar();
                                    }
                                }>
                                    <img className={ToolbarStyle.icon} src={item.metaData.icon}/>
                                    <div>{item.metaData.text}</div>
                                </button>
                            )
                        })
                    }
                </div>
                <div className={ToolbarStyle["toolbar-outside"]}
                     onClick={this.closeToolbar}>
                </div>
            </div>
        )
    }
}

Toolbar.propTypes = {
    context: PropTypes.object.isRequired,
    posX: PropTypes.number.isRequired,
    posY: PropTypes.number.isRequired,
    visible: PropTypes.bool.isRequired,
    closeToolbar: PropTypes.func.isRequired
};

const mapStateToProps = state => {
    return {
        context: state.toolbar.context,
        posX: state.toolbar.posX,
        posY: state.toolbar.posY,
        visible: state.toolbar.visible
    };
};

const mapDispatchToProps = dispatch => {
    return {
        closeToolbar: () => dispatch(closeToolbar())
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Toolbar);
