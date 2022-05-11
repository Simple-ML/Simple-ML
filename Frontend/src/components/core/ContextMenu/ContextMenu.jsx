//node_module
import React from 'react';
import { connect } from 'react-redux';
import PropTypes, { string } from 'prop-types';
import { Backdrop, TextField, Tooltip } from '@mui/material';
//redux
import InferenceCreator from './InferenceCreator';
import { closeContextMenu } from '../../../reducers/contextMenu';
//services
import XtextServices from '../../../serverConnection/XtextServices';
//style
import ContextMenuStyle from './contextMenu.module.scss';
//icons
import editIcon from '../../../images/contextToolbar/Edit.svg';
import linearRegressionIcon from '../../../images/contextToolbar/Linear.svg';
import decisionTreeClassifierIcon from '../../../images/contextToolbar/DecisionTree.svg';
import supportVectorMachineClassifierIcon from '../../../images/contextToolbar/Neuro.svg';
import loadDatasetIcon from '../../../images/contextToolbar/LoadDataset.svg';
import joinIcon from '../../../images/contextToolbar/Join.svg';
import splitIcon from '../../../images/contextToolbar/Split.svg';
import predictIcon from '../../../images/contextToolbar/Prediction.svg';
import exportIcon from '../../../images/contextToolbar/Export.svg';
import regressionIcon from '../../../images/contextToolbar/Regression.svg';

class ContextMenu extends React.Component {

    mapNameIcon = {
        'LinearRegression': linearRegressionIcon,
        'LinearRegressionModel': linearRegressionIcon,
        'DecisionTreeClassifier': decisionTreeClassifierIcon,
        'DecisionTreeClassifierModel': decisionTreeClassifierIcon,
        'SupportVectorMachineClassifier': supportVectorMachineClassifierIcon,
        'SupportVectorMachineClassifierModel': supportVectorMachineClassifierIcon,
				'loadDataset': loadDatasetIcon,
				'joinTwoDatasets': joinIcon,
				'splitIntoTrainAndTest': splitIcon,
				'splitIntoTrainAndTestAndLabels': splitIcon,
				'predict': predictIcon,
				'exportDataAsFile': exportIcon,
				'RidgeRegression': regressionIcon,
				'RidgeRegressionModel': regressionIcon,
    }

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
            searchTerm: '',
            validInput: [true]
        };
    }
  
    filterList(event) {
      this.setState({searchTerm: event.target.value});
    }

    clearState = () => {
        this.setState({
            contextButtonFunc: () => {},
            isBackdropActive: false,
            placeholderName: '',
            validInput: [true]
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
                console.log('ML test item: ' + context2?.frontendId + ' -> ' + JSON.stringify(item));

                const icon = this.mapNameIcon[item.name] ?? editIcon;

                result.push({
                    metaData: {
                        icon: icon,
                        text: item.name,
                        classReference: item.containingClassName,
                        toolTip: item.description,
						needInputData: true
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

        const inferedStatic = InferenceCreator.inferFromContext(this.props.context);
        const inferedDynamic = this.inferFromContextDynamically(this.props.context, this.props.proposals);

        const buttonMetaData = [...inferedStatic, ...inferedDynamic];
        this.prepareMetaData(buttonMetaData);

        let { posX, posY, visible } = this.props;

        visible = visible && buttonMetaData.length > 0;
        visible = visible ? 'visible' : 'hidden';

        return(
            <div style={{visibility: visible}}>
                <div className={ContextMenuStyle["toolbar"]}
                    style={{top: posY, left: posX}} ref={this.myself}>
                    <input type={'text'} value={this.state.searchTerm} onChange={this.filterList}
                      placeholder={'Search Action'} className={ContextMenuStyle["toolbar-text-filter"]}></input>
                    <div className={ContextMenuStyle["toolbar-button-container"]}>
                        {
                          buttonMetaData.sort((a, b) => a.metaData.text.localeCompare(b.metaData.text)).filter(item => item.metaData.text.toLowerCase().includes(this.state.searchTerm.toLowerCase()))
                            .map((item, i) => {
                                return(
                                    <Tooltip key={i}
                                        title={item.metaData.toolTip ? item.metaData.toolTip : ""}>
                                        <button className={ContextMenuStyle["toolbar-button"]}
                                            disabled={item.metaData.disabled()}
                                            onClick={() => {
                                                this.setState({
                                                    contextButtonFunc: item.func,
                                                    isBackdropActive: item.metaData.needInputData === true
                                                })
                                                if (item.metaData.needInputData === false) {
                                                    item.func();
                                                    this.props.closeContextMenu();
                                                }
                                            }
                                        }>
                                            <img className={ContextMenuStyle["button-icon"]} src={item.metaData.icon}/>
                                            <div className={ContextMenuStyle["button-name"]}>{item.metaData.text}</div>
                                            <div className={ContextMenuStyle["button-className"]}>{item.metaData.classReference}</div>
                                        </button>
                                    </Tooltip>
                                )
                            })
                        }
                    </div>
                </div>
                <div className={ContextMenuStyle["toolbar-outside"]}
                     onClick={this.props.closeContextMenu}>
                </div>
                <Backdrop
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
                                this.setState({placeholderName: e.target.value});

                                if(e.target.value.match('[a-z]')) {
                                    this.setState({validInput: [true]})
                                } else {
                                    this.setState({validInput: [false]})
                                }
                            }
                        }>
                        </TextField>
                        <div className={ContextMenuStyle["assign-placeholder-modal-button-container"]}>
                            <button className={ContextMenuStyle["assign-placeholder-modal-cancel-button"]}
                                onClick={() => {
                                    this.clearState();
                                    this.props.closeContextMenu();
                                }
                            }>
                                Cancel
                            </button>
                            <button className={ContextMenuStyle["assign-placeholder-modal-create-button"]}
                                disabled={!this.state.validInput[0]}
                                onClick={() => {
                                    this.createEntity();
                                }
                            }>
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
