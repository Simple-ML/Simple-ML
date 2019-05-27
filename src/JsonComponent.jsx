import React from 'react';
import XtextServices from "./ServerConnection/xtextServices";

class JsonComponent extends React.Component{
    constructor() {
        super()

        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                console.log("consturctor");
            }
        });
    }

    render(){
        XtextServices.addSuccessListener((serviceType, result) => {
            if (serviceType === 'json') {
                console.log("render");
            }
        });
        return (
            <div> </div>
        );
    }
}
export default JsonComponent;
