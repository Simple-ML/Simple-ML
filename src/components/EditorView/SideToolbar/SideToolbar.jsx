import React from 'react';


class SideToolbar extends React.Component {
    constructor(props) {
        super(props);



    }

    render() {
        if(this.props.layout === undefined)
            return (<div className={'SideToolbar'}></div>);

        return(
            <div className={'SideToolbar'}>
                { this.props.elementConfigs.map((element) => {
                    <button>

                        element.title
                    </button>
                }) }
            </div>
        )
    }
}

