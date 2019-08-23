import React, { Component } from "react";

export const EditorContext = React.createContext();
export const provideRef = Component => React.forwardRef((props,ref) => (
    <EditorContext.Consumer>
        {something => <Component {...props} something={something} ref={ref}/>}
    </EditorContext.Consumer>
))