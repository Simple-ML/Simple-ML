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
import { showSideToolbar, hideSideToolbar } from '../../reducers/sideToolbar';

//style
import './editorView.scss'
import 'golden-layout/src/css/goldenlayout-base.css';
import headerStyle from '../core/Header/header.module.scss';
//images
import viewbarIcon from '../../images/headerButtons/viewbar-closed.svg';

//Datasets
import DataView from './DataView/DataView';
import Backdrop from '@mui/material/Backdrop';
import IconButton from '@mui/material/IconButton';
import Sidebar from './MultiView/Sidebar/Sidebar';
import Icons from '../../stories/Icons';

import { hideDataViewBackdrop } from '../../reducers/graphicalEditor';

class EditorView extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
        }
        
        this.showHideSideToolbar = this.showHideSideToolbar.bind(this);
        this.flipGraph = this.flipGraph.bind(this);
    }

    showHideSideToolbar = () => {
        if(this.props.isSideToolbarVisible)
            this.props.hideSideToolbar();
        else
            this.props.showSideToolbar();
    }

    flipGraph = () => {
        this.props.changeDirection();
    }

    handleClose = () => {
        this.props.hideDataViewBackdrop();
    };

    render() {
        console.log(this.props.isDataviewBackdropActive);
        return(
            <div className={'editor-view'}>
                <EditorHeader>
                    <input className={headerStyle.button}
                        key={1}
                        type={'image'} src={viewbarIcon}
                        onClick={() => this.showHideSideToolbar()}
                    />
                </EditorHeader>
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
        )
    }
}

EditorView.propTypes = {
    isSideToolbarVisible: PropTypes.bool.isRequired,
    changeDirection: PropTypes.func.isRequired,
    showSideToolbar: PropTypes.func.isRequired,
    hideSideToolbar: PropTypes.func.isRequired,

    hideDataViewBackdrop: PropTypes.func.isRequired
};

const mapStateToProps = state => {
    return {
        isSideToolbarVisible: state.sideToolbar.visible,
        isDataviewBackdropActive: state.graphicalEditor.dataviewBackdropActive
    }
};

const mapDispatchToProps = dispatch => {
    return {
        changeDirection: () => dispatch(changeDirection()),
        showSideToolbar: () => dispatch(showSideToolbar()),
        hideSideToolbar: () => dispatch(hideSideToolbar()),
    
        hideDataViewBackdrop: () => dispatch(hideDataViewBackdrop())
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(EditorView);
