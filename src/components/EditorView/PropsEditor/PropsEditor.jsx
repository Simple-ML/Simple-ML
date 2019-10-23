//node_module
import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
//redux
import { closePropsEditor } from '../../../reducers/propsEditor';
//style
import PropsEditorStyle from './propsEditor.module.scss';
//React.Components
import PropsDetails from './PropsDetails.jsx'
class PropsEditor extends React.Component {
    constructor(props) {
        super(props);

        this.myself = React.createRef();
        this.closePropsEditor = this.closePropsEditor.bind(this);
    }

    closePropsEditor = () => {
        this.props.closePropsEditor()
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
        let { visible } = this.props;
        visible = visible ? 'visible' : 'hidden';
        if (this.props.context.value){
            return(
                <div style={{visibility: visible}}>
                    <div className={PropsEditorStyle.propsEditor}
                        ref={this.myself}>
                        <PropsDetails/>
                    </div>
                    <div className={PropsEditorStyle["props-editor-outside"]}
                        onClick={this.closePropsEditor}>
                    </div>
                </div>
            )
        }
        else return(
            <div style={{visibility: visible}}></div>
        );
    }
}

PropsEditor.propTypes = {
    context: PropTypes.object.isRequired,
    visible: PropTypes.bool.isRequired,
    closePropsEditor: PropTypes.func.isRequired
};

const mapStateToProps = state => {
    return {
        context: state.propsEditor.context,
        visible: state.propsEditor.visible
    };
};

const mapDispatchToProps = dispatch => {
    return {
        closePropsEditor: () => dispatch(closePropsEditor())
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(PropsEditor);
