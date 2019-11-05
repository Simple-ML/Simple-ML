import React from 'react';

class ParameterInput extends React.Component {
    constructor(props){
        super(props);
        this.state={
            name: props.name,
            type: props.type,
            value:props.value
        }
    } 

    handleChange(){
        console.log("text changed")
    }
    
    render(){
    let {key, name, type, value} = this.state;
    let handleChange= this.handleChange.bind(this);
        return(
            <div>
                <div>
                name: {name}
                </div>
                <div>
                type: {type}
                </div>
                <input onChange={handleChange} value={value}>

                </input>
            </div>
        )
    }
}
export default ParameterInput;