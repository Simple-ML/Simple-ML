
// node_modules
import React from 'react';
import { connect } from 'react-redux';
import $ from "jquery";


// React.Components
import GoldenLayoutComponent from './../../../helper/goldenLayoutServices/goldenLayoutComponent';
import Sidebar from './Sidebar/Sidebar'
import Toolbar from './Toolbar/Toolbar';

// Styles
import './multiView.scss';

// Config
import MultiViewConfig from './MultiViewConfig';

import store from '../../../reduxStore'

class MultiView extends React.Component {
    constructor(props) {
        super(props);

        this.onStoreChange = this.onStoreChange.bind(this);
  
        this.unsubscribe = store.subscribe(() => {
            this.setState(this.onStoreChange(store.getState()));
        });
  
        this.state = this.onStoreChange(store.getState());
    }

    onStoreChange = (state) => {

        return {
            selectedEntity: state.graphicalEditor.entitySelected,
            placeholders: state.runtime?.placeholder
        };
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
        const { placeholders, selectedEntity } = this.state;
        return (
            <div className={'multi-view-container'}>
                <Toolbar componentConfigs={MultiViewConfig.getPureConfigList()} layout={this.state.myLayout} />
                <GoldenLayoutComponent
                    htmlAttrs={{ style: { heigth: "780px", width: "100%" } }}
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
                {
                    Object.keys(placeholders).length !== 0 && Object.keys(selectedEntity).length !== 0?
                        <Sidebar></Sidebar>
                        : <div></div>
                }
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
