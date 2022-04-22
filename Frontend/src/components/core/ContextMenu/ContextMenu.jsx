//node_module
import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { Backdrop, TextField } from '@mui/material';
//redux
import InferenceCreator from './InferenceCreator';
import { closeContextMenu } from '../../../reducers/contextMenu';
//services
import XtextServices from '../../../serverConnection/XtextServices';
//style
import ContextMenuStyle from './contextMenu.module.scss';
//icons
import editIcon from '../../../images/contextToolbar/Edit.svg';

class ContextMenu extends React.Component {
    constructor(props) {
        super(props);

        this.myself = React.createRef();

        this.inferFromContextDynamically = this.inferFromContextDynamically.bind(this);
        this.prepareMetaData = this.prepareMetaData.bind(this);
        this.filterList = this.filterList.bind(this);

        this.state = {
            contextButtonFunc: () => {},
            isBackdropActive: false,
            placeholderName: '',
            searchTerm: ''
        };
    }
  
    filterList(event) {
      this.setState({searchTerm: event.target.value});
    }

    clearState = () => {
        this.setState({
            contextButtonFunc: () => {},
            isBackdropActive: false,
            placeholderName: ''
        });
    }

    createEntity = () => {
        this.state.contextButtonFunc(this.state.placeholderName);
        this.clearState();
        this.props.closeContextMenu();
    }

    inferFromContextDynamically = (context, context2) => {
        let result = [];

        if(context?.emfReference?.id + '' === context2?.frontendId) {
            context2.proposals?.forEach((item) => {
                result.push({
                    metaData: {
                        icon: editIcon,
                        text: item.name
                    },
                    func: (placeholderName) => {
                        XtextServices.createEntity({
                            className: '',
                            referenceIfFunktion: item.emfPath, 
                            placeholderName: placeholderName,
                            associationTargetPath: context.associationTargetPath || ''
                        });
                    }
                });
            });
        }
        return result;
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
         * Expected MetaData[]:
         *     metadata: {
         *         icon: object,                        // source or path of image what can be passed to src-parameter in a html-tag
         *         text: string                         // will be displayed
         *         disabled: () => { returns bool }     // indicates if func (below) can be executed
         *     },
         *     func: () => { returns void }             // function to be executed (div->onClick())
         * @type {Array}
         */
        let buttonMetaData = [];
        buttonMetaData = buttonMetaData.concat(InferenceCreator.inferFromContext(this.props.context));
        buttonMetaData = buttonMetaData.concat(this.inferFromContextDynamically(this.props.context, this.props.proposals));
        this.prepareMetaData(buttonMetaData);

        let { posX, posY, visible } = this.props;

        visible = visible && buttonMetaData.length > 0;
        visible = visible ? 'visible' : 'hidden';

        return(
          <div style={{ visibility: visible }}>
                <div className={ContextMenuStyle.toolbar}
                    style={{top: posY, left: posX}} ref={this.myself}>
                    <input type={'text'} value={this.state.searchTerm} onChange={this.filterList} placeholder={'Search Action'} className={ContextMenuStyle["text-filter"]}></input>
                    {
                        buttonMetaData.sort((a, b) => a.metaData.text.localeCompare(b.metaData.text)).filter(item => item.metaData.text.toLowerCase().includes(this.state.searchTerm.toLowerCase())).map((item, i) => {
                            return(
                                <button className={ContextMenuStyle["toolbar-button"]}
                                    key={i}
                                    disabled={item.metaData.disabled()}
                                    onClick={() => {
                                        this.setState({
                                            contextButtonFunc: item.func,
                                            isBackdropActive: true
                                        })
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
                <Backdrop
                    // style= {{backgroundColor:'white', opacity: '0.3'}}
                    sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                    open={this.state.isBackdropActive}
                >
                    <div className={ContextMenuStyle["assign-placeholder-modal"]}>
                        <div className={ContextMenuStyle["assign-placeholder-modal-headline"]}>
                            Assign a name
                        </div>
                        <TextField className={ContextMenuStyle["assign-placeholder-modal-text-field"]}
                            id="outlined-basic" label="Name" variant="outlined" value={this.state.placeholderName}
                            onChange={(e) => {
                                e.persist();
                                // this.state.placeholderName = e.target.value;
                                this.setState({placeholderName: e.target.value})
                            }}
                            onKeyDown={(e) => {
                                if(e.keyCode === 13)
                                    this.createEntity();
                            }}>
                        </TextField>
                        <div className={ContextMenuStyle["assign-placeholder-modal-button-container"]}>
                            <button className={ContextMenuStyle["assign-placeholder-modal-cancel-button"]}
                                onClick={() => {
                                    this.clearState();
                                    this.props.closeContextMenu();
                                }}>
                                Cancel
                            </button>
                            <button className={ContextMenuStyle["assign-placeholder-modal-create-button"]}
                                onClick={() => {
                                    this.createEntity();
                                }}>
                                Create
                            </button>
                        </div>
                    </div>
                </Backdrop>
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
        visible: state.contextMenu.visible,
        proposals: state.emfModel.processProposals
    };
};

const mapDispatchToProps = dispatch => {
    return {
        closeContextMenu: () => dispatch(closeContextMenu())
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(ContextMenu);
