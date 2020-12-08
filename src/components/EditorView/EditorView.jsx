//node_modules
import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import $ from "jquery";

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


class EditorView extends React.Component {
    constructor(props) {
        super(props);
        
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

    

    render() {
        return(
            <div className={'editor-view'}>
                <EditorHeader>
                    <input className={headerStyle.button}
                       key={1}
                       type={'image'} src={viewbarIcon}
                       onClick={() => this.showHideSideToolbar() }/>
                </EditorHeader>
                <MultiView 
                    showAtStartup={[
                        'graphicalEditor',
                        'textEditor'
                    ]}
                />
                <PropsEditor/>
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
