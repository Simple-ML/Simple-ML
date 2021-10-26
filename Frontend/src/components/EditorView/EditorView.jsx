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
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';

class EditorView extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            backdropActive: false
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

    handleToggle = () => {
        this.setState({ backdropActive: !this.state.backdropActive })
    }

    handleClose = () => {
        this.setState({ backdropActive: false })
    };

    render() {
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
                <Button onClick={this.handleToggle}>Show dataset</Button>
                <Backdrop
                  sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                  open={this.state.backdropActive}
                >
                    <DataView
                        url='data/example_profile_adac.json'
                    />
                    <IconButton 
                        sx={{ color: '#fff'}}
                        onClick={this.handleClose}>
                        close
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
    hideSideToolbar: PropTypes.func.isRequired
};

const mapStateToProps = state => {
    return {
        isSideToolbarVisible: state.sideToolbar.visible
    }
};

const mapDispatchToProps = dispatch => {
    return {
        changeDirection: () => dispatch(changeDirection()),
        showSideToolbar: () => dispatch(showSideToolbar()),
        hideSideToolbar: () => dispatch(hideSideToolbar())
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(EditorView);
