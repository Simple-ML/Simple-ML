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

import { hideDataViewBackdrop } from '../../reducers/graphicalEditor';

class EditorView extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
        }
        
        this.showHideToolbar = this.showHideToolbar.bind(this);
        this.flipGraph = this.flipGraph.bind(this);
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

    render() {
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
                    <Backdrop
                        style= {{backgroundColor:'white'}}
                        sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                        open={this.props.isDataviewBackdropActive}
                    >
                        <DataView
                            url='data/example_profile_adac.json'
                        />
                        <IconButton 
                            style= {{marginBottom: 'auto'}}
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
