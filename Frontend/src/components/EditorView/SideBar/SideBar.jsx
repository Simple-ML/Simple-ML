import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import SideBarStyles from './sideBar.module.scss';


class SideBar extends React.Component {

    render() {
        return(
            <div className={SideBarStyles['SideBar']}>
                Sidebar
            </div>
    )};
}


SideBar.propTypes = {
    selectedEntity: PropTypes.object.isRequired
    
};

const mapStateToProps = state => {
    return {
        selectedEntity: state.graphicalEditor.entitySelected
    }
};

const mapDispatchToProps = dispatch => {
    return {
        
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(SideBar);