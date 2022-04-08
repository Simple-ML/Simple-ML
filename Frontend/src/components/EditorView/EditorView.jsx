//node_modules
import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
    
//React.Components
import EditorHeader from './EditorHeader/EditorHeader';
import MultiView from './MultiView/MultiView';
import PropsEditor from './PropsEditor/PropsEditor';

//redux
import { changeDirection } from '../../reducers/graphicalEditor';
import { showToolbar, hideToolbar } from '../../reducers/toolbar';

//style
import editorStyle from './editorView.module.scss';
import headerStyle from '../core/Header/header.module.scss';
import 'golden-layout/src/css/goldenlayout-base.css';

//images
import viewbarIcon from '../../images/headerButtons/viewbar-closed.svg';

//Datasets
import DataView from './DataView/DataView';
import Backdrop from '@mui/material/Backdrop';
import Icons from '../../stories/Icons';
import IconButton from '@mui/material/IconButton';

import { hideDataViewBackdrop } from '../../reducers/graphicalEditor';
import store from '../../reduxStore';
import Sidebar from './Sidebar/Sidebar'

class EditorView extends React.Component {
    constructor(props) {
        super(props);

        this.onStoreChange = this.onStoreChange.bind(this);

        this.unsubscribe = store.subscribe(() => {
            this.setState(this.onStoreChange(store.getState()));
        });
  
        this.state = this.onStoreChange(store.getState());
        
        this.showHideToolbar = this.showHideToolbar.bind(this);
        this.flipGraph = this.flipGraph.bind(this);
    }

    onStoreChange = (state) => {

        return {
            selectedEntityDataset: this.getDataset(state.graphicalEditor.entitySelected, state.runtime?.placeholder),
            selectedEntity: state.graphicalEditor.entitySelected,
            placeholders: state.runtime?.placeholder
        };
      }  

    showHideToolbar = () => {
        if(this.props.isToolbarVisible)
            this.props.hideToolbar();
        else
            this.props.showToolbar();
    }

    flipGraph = () => {
        this.props.changeDirection();
    }

    handleClose = () => {
        this.props.hideDataViewBackdrop();
    };    
    
    getDataset = (selectedEntity, placeholders) => {
        for (const [key, value] of Object.entries(placeholders)) {
            if(key === selectedEntity?.data?.name) {
                return value;
            }
        }
        return '';
    }   

    render() {
        const { selectedEntityDataset, placeholders, selectedEntity  } = this.state;
        return(
            <div className={editorStyle['editor-view']}>
                <EditorHeader>
                    <input className={headerStyle.button}
                       key={1}
                       type={'image'} src={viewbarIcon}
                       onClick={() => this.showHideToolbar() }/>
                </EditorHeader>
                <div style={{display: 'flex'}}>
                    <MultiView 
                        showAtStartup={[
                            'graphicalEditor',
                            'textEditor'
                        ]}
                    />
                    <Sidebar></Sidebar>
                    <Backdrop
                        style= {{backgroundColor:'white', overflow: 'hidden'}}
                        sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                        open={this.props.isDataviewBackdropActive}
                    >
                        { selectedEntityDataset ?
                            <DataView
                                dataset={selectedEntityDataset}
                            /> : <div></div>
                        }
                        
                        <IconButton 
                            style= {{marginBottom: 'auto', position: 'absolute', top: '5px', right: '0px'}}
                            sx={{ color: '#fff'}}
                            onClick={this.handleClose}>
                            <Icons icons="close" color="black"/>
                        </IconButton>
                    </Backdrop>
                </div>
            </div>
        )
    }
}

EditorView.propTypes = {
    isToolbarVisible: PropTypes.bool.isRequired,
    changeDirection: PropTypes.func.isRequired,
    showToolbar: PropTypes.func.isRequired,
    hideToolbar: PropTypes.func.isRequired,
    hideDataViewBackdrop: PropTypes.func.isRequired
};

const mapStateToProps = state => {
    return {
        isToolbarVisible: state.toolbar.visible,
        isDataviewBackdropActive: state.graphicalEditor.dataviewBackdropActive
    }
};

const mapDispatchToProps = dispatch => {
    return {
        changeDirection: () => dispatch(changeDirection()),
        showToolbar: () => dispatch(showToolbar()),
        hideToolbar: () => dispatch(hideToolbar()),
        hideDataViewBackdrop: () => dispatch(hideDataViewBackdrop())
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(EditorView);
