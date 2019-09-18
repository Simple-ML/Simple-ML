import React from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'react-redux';

import './SideToolbar.scss';
import PropTypes from "prop-types";

/**
 * This component is used in combination with golden-layout. So this.props.layout has to be the layout-Object from
 * golden-layout, (caution) what is commonly initialised after render in componentDidMount.
 */
class SideToolbar extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            childInfos: []
        }
        this.onlyOnce = false;
    }

    /**
     * TODO: document why
     */
    componentDidUpdate() {
        if(this.props.layout !== undefined && !this.onlyOnce) {
            this.onlyOnce = true;
            this.state.childInfos.forEach((child) => {
                this.props.layout.createDragSource(ReactDOM.findDOMNode(child.ref), child.config);
            });
        }
    }

    render() {
        let style = {
            visibility: this.props.visible ? 'visible' : 'hidden'
        };

        return (
            <div className={'Side-Toolbar'} style={style}>
                {   this.state.childInfos = []   }
                {   this.props.componentConfigs.map((componentConfig, i) => {
                return (
                    <button key={i}
                        ref={(input) => {
                            this.state.childInfos.push({ref: input, config: componentConfig});
                        }}
                        onClick={() => {
                            this.props.layout.root.contentItems[0].addChild(componentConfig)
                        }}
                    >
                        {componentConfig.title}
                    </button>)
                })}
            </div>
        )
    }
}

SideToolbar.propTypes = {
    visible: PropTypes.bool.isRequired
};

const mapStateToProps = state => {
    return {
        visible: state.sideToolbar.visible
    }
};

const mapDispatchToProps = dispatch => {
    return {
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(SideToolbar);
