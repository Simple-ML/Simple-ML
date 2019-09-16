//node_modules
import React from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
//React.Components
import EditorHeader from './EditorHeader/EditorHeader';
import SideToolbar from './SideToolbar/SideToolbar';
import GraphicalEditor from './GraphicalEditor/GraphicalEditor';
import TextEditor from './TextEditor/TextEditor';
import { GoldenLayoutComponent } from './../../helper/goldenLayoutServices/goldenLayoutComponent';

import XtextServices from '../../serverConnection/XtextServices';
import { changeDirection } from '../../reducers/graphicalEditor';
//style
import './editorView.scss'
import 'golden-layout/src/css/goldenlayout-base.css';


class EditorView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            myLayout: undefined,
            draggable: undefined
        };

        this.flipGraph = this.flipGraph.bind(this)
    }

    getElementConfigs = () => {
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

    flipGraph = () => {
        this.props.changeDirection();
    };

    wrapComponent = Component => {
        class Wrapped extends React.Component {
            render() {
                return (
                    <Component {...this.props}/>
                );
            }
        }
        return Wrapped;
    };

    componentDidMount() {
        let element = this.state.draggable;

        this.state.myLayout.createDragSource(element,  {
            title: "Graphical Editor",
            type: "react-component",
            component: "graphicalEditor"
        });
    }

    render() {
        return(
            <div className='EditorView'>
                <EditorHeader />
                <SideToolbar elementConfigs={this.getElementConfigs()} layout={this.state.myLayout} />
                <div className={'buttons'}>
                    <button style={{ color: 'black' }} onClick={() => { XtextServices.getEmfModel(); }}>
                        {'Get EMF-Model'}
                    </button>
                    <button style={{ color: 'black' }} onClick={() => this.flipGraph()}>
                        { 'Flip The Graph' }
                    </button>
                    <a ref={(input) => {
                        this.state.draggable = input
                    }}>
                        just some text
                    </a>
                </div>
                <div className='ide-container'>
                    <GoldenLayoutComponent
                        htmlAttrs={{ style: { height: "500px", width: "100%" } }}
                        config={{
                            content:[{
                                type: "row",
                                content: [{
                                    title: "DSL Editor",
                                    type: "react-component",
                                    component: "textEditor",
                                },
                                {
                                    title: "Graphical Editor",
                                    type: "react-component",
                                    component: "graphicalEditor",
                                }]
                            }]
                        }}
                        registerComponents = { myLayout => {
                            myLayout.registerComponent("textEditor", this.wrapComponent(TextEditor));
                            myLayout.registerComponent("graphicalEditor", this.wrapComponent(GraphicalEditor))
                            this.state.myLayout = myLayout
                        }}
                    />
                </div>
            </div>
        )
    }
}

EditorView.propTypes = {
    changeDirection: PropTypes.func.isRequired
}

const mapStateToProps = state => {
    return {
    };
};

const mapDispatchToProps = dispatch => {
    return {
        changeDirection: () => dispatch(changeDirection())
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(EditorView);
