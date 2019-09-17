import React from 'react';
import ReactDOM from 'react-dom';


class SideToolbar extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            layout: this.props.layout,
            childInfo: []
        }

    }

    componentDidUpdate() {
        if(this.props.layout !== undefined) {
            this.state.childInfo.forEach((child) => {
                this.props.layout.createDragSource(ReactDOM.findDOMNode(child.ref), child.config);
            });
        }
    }

    render() {
        return (
            <div className={'SideToolbar'}>
                {   this.state.childInfo = []   }
                {   this.props.componentConfigs.map((componentConfig, i) => {
                    return (
                        <button key={i}
                            ref={(input) => {
                                this.state.childInfo.push({ref: input, config: componentConfig});
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

export default SideToolbar;
