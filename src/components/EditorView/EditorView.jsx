//node_modules
import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import $ from "jquery";

//React.Components
import EditorHeader from './EditorHeader/EditorHeader';
import SideToolbar from './SideToolbar/SideToolbar';
import GraphicalEditor from './GraphicalEditor/GraphicalEditor';
import DetailsEditor from "./DetailsEditor/DetailsEditor";
import PropsEditor from "./PropsEditor/PropsEditor";
import TextEditor from './TextEditor/TextEditor';
import GoldenLayoutComponent from './../../helper/goldenLayoutServices/goldenLayoutComponent';
//redux
import { changeDirection } from '../../reducers/graphicalEditor';
import { showSideToolbar, hideSideToolbar } from '../../reducers/sideToolbar';

//style
import './editorView.scss'
import 'golden-layout/src/css/goldenlayout-base.css';
import headerStyle from '../core/Header/header.module.scss';
//images
import viewbarIcon from '../../images/headerButtons/viewbar-closed.svg';
import graphicalEditorIcon from '../../images/sideToolbar/flow.svg';
import textEditorIcon from '../../images/sideToolbar/text-ide.svg';
import detailViewIcon from '../../images/sideToolbar/chart.svg';
import tutorialIcon from '../../images/sideToolbar/tutorial.svg';

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
            component: "graphicalEditor",
            icon: graphicalEditorIcon
        },
        {
            title: "Text-Editor",
            type: "react-component",
            component: "textEditor",
            icon: textEditorIcon
        },
        {
            title: "Details",
            type: "react-component",
            component: "detailsEditor",
            icon: detailViewIcon
        },
        {
            title: "Tutorial",
            type: "react-component",
            component: "tutorial",
            icon: tutorialIcon
        },
        {
            title: "Properties Editor",
            type: "react-component",
            component: "propsEditor",
            icon: tutorialIcon
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
        let componentConfigs = this.createComponentConfigs();

        return(
            <div className={'EditorView'}>
                <EditorHeader>
                    <input className={headerStyle.button}
                       key={1}
                       type={'image'} src={viewbarIcon}
                       onClick={() => this.showHideSideToolbar() }/>
                </EditorHeader>
                <SideToolbar componentConfigs={componentConfigs} layout={this.state.myLayout} />
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
                                        type: 'column',
                                        content:[
                                            componentConfigs[0],
                                            componentConfigs[1]
                                        ]
                                    },
                                ]
                            }]
                        }}
                        registerComponents={myLayout => {
                            myLayout.registerComponent("textEditor",  this.wrapComponent(TextEditor));
                            myLayout.registerComponent("graphicalEditor",  this.wrapComponent(GraphicalEditor));
                            myLayout.registerComponent("detailsEditor",  this.wrapComponent(DetailsEditor));
                            myLayout.registerComponent("tutorial",  this.wrapComponent(DetailsEditor));
                            myLayout.registerComponent("propsEditor",  this.wrapComponent(PropsEditor));
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
                    <PropsEditor/>
                </div>
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
