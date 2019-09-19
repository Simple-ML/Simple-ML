//node_modules
import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import $ from "jquery";

//React.Components
import EditorHeader from './EditorHeader/EditorHeader';
import SideToolbar from './SideToolbar/SideToolbar';
import GraphicalEditor from './GraphicalEditor/GraphicalEditor';
import DetailsEditor from "./DetailsEditor/DetailsEditor"
import TextEditor from './TextEditor/TextEditor';
import GoldenLayoutComponent from './../../helper/goldenLayoutServices/goldenLayoutComponent';
import DefaultModal from '../core/Modal/DefaultModal';
//redux
import { showModal } from "../../reducers/modal";
import { changeDirection } from '../../reducers/graphicalEditor';
//style
import './editorView.scss'
import 'golden-layout/src/css/goldenlayout-base.css';
import { showSideToolbar, hideSideToolbar } from "../../reducers/sideToolbar";


class EditorView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            myLayoutWrapper: {
                layout: undefined,
                isSideToolbarVisible: undefined
            }
        };

        this.showHideSideToolbar = this.showHideSideToolbar.bind(this);
        this.flipGraph = this.flipGraph.bind(this);
    }

    createComponentConfigs = () => {
        return [{
            title: "Graphical Editor",
            type: "react-component",
            component: "graphicalEditor"
        },
        {
            title: "Text-Editor",
            type: "react-component",
            component: "textEditor"
        }]
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

    render() {
        return(
            <div className={'EditorView'}>
                <EditorHeader />
                <SideToolbar componentConfigs={this.createComponentConfigs()} layout={this.state.myLayout} />

                <div className={'buttons'}>
                    <button style={{ color: 'black' }} onClick={() => this.showHideSideToolbar() }>
                        {'Toolbar'}
                    </button>
                    <button style={{ color: 'black' }} onClick={() => this.flipGraph()}>
                        { 'Flip The Graph' }
                    </button>
                </div>
                <div className={'ide-container'}>
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
                                        title: "Graphical Editor",
                                        type: "react-component",
                                        component: "graphicalEditor"
                                    },
                                    {
                                        type: 'column',
                                        content:[
                                            {
                                                title: "DSL Editor",
                                                type: "react-component",
                                                component: "textEditor",
                                                height: 68.803
                                            },
                                            {
                                                title: "Details",
                                                type: "react-component",
                                                component: "detailsEditor",
                                            }
                                    ]
                                }]

                            }]
                        }}
                        registerComponents={myLayout => {
                            myLayout.registerComponent("textEditor",  this.wrapComponent(TextEditor));
                            myLayout.registerComponent("graphicalEditor",  this.wrapComponent(GraphicalEditor));
                            myLayout.registerComponent("detailsEditor",  this.wrapComponent(DetailsEditor));
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
            </div>
        )
    }
}

EditorView.propTypes = {
    isSideToolbarVisible: PropTypes.bool.isRequired,

    changeDirection: PropTypes.func.isRequired,
    showSideToolbar: PropTypes.func.isRequired,
    hideSideToolbar: PropTypes.func.isRequired,
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
        hideSideToolbar: () => dispatch(hideSideToolbar()),
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(EditorView);
