//node_module
import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
//style
import PropsEditorStyle from './propsEditor.module.scss';

class PropsEditor extends React.Component {
    constructor(props) {
        super(props);

    }

    render() {
        if (this.props.entitySelected.data){
            const PropsEditorEntityComponent = this.props.entitySelected.metadata.propsEditorMetadata.component;
            
            return(
                <div className={PropsEditorStyle.propsEditor}>
                    <PropsEditorEntityComponent entity={this.props.entitySelected}/>
                </div>
            )
        }
        else return(
            <div></div>
        );
    }
}

PropsEditor.propTypes = {
    entitySelected: PropTypes.object.isRequired
};

const mapStateToProps = state => {
    return {
        entitySelected: state.graphicalEditor.entitySelected
    };
};

const mapDispatchToProps = dispatch => {
    return {
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(PropsEditor);
