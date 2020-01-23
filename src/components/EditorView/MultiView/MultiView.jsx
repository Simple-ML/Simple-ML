
// node_modules
import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import $ from "jquery";

// React.Components
import GoldenLayoutComponent from './../../../helper/goldenLayoutServices/goldenLayoutComponent';
import SideToolbar from './SideToolbar/SideToolbar';

// Styles
import multiViewStyle from './multiView.module.scss';

// Config
import MultiViewConfig from './MultiViewConfig';

class MultiView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    wrapComponent = Component => {
        class Wrapped extends React.Component {
            render() {
                return (
                    <Component {...this.props}/>
                );
            }
        }
        return Wrapped;
    }

    whatToShowAtStartup = () => {
        return this.props.showAtStartup.map((item) => {
            return MultiViewConfig.getComponentConfigByName(item).config;
        });
    }

    render() {
        return (
            <div className={multiViewStyle['multi-view-container']}>
                <SideToolbar componentConfigs={MultiViewConfig.getPureConfigList()} layout={this.state.myLayout} />
                <GoldenLayoutComponent
                    htmlAttrs={{ style: { minHeight: "780px", width: "100%" } }}
                    config={{
                        dimensions:{
                            headerHeight: "100%"
                        },
                        content:[{
                            type: "row",
                            content: [
                                {
                                    type: 'column',
                                    content: this.whatToShowAtStartup()
                                },
                            ]
                        }]
                    }}
                    registerComponents={myLayout => {
                        MultiViewConfig.getComponentConfigList().forEach((item) => {
                            console.log(item)
                            myLayout.registerComponent(item.config.component, this.wrapComponent(item.component));
                        });
                        this.setState({myLayout});
                        /*
                        * Since our layout is not a direct child
                        * of the body we need to tell it when to resize
                        */
                        $(window).on("resize", function(){
                            myLayout.updateSize();
                        })
                    }}
                />
            </div>
        )
    }
}


MultiView.propTypes = {

};

const mapStateToProps = state => {
    return {
    }
};

const mapDispatchToProps = dispatch => {
    return {
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(MultiView);
