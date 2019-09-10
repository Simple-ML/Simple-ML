import React from 'react';
import './App.scss';
import EditorView from './components/EditorView/EditorView';
import LoginView from './components/LoginView/LoginView';
import ChooseProjectView from './components/EditorView/ChooseProjectView/ChooseProjectView';


class App extends React.Component {
    render() {
        return (
            <div className="App">
                {/*<ChooseProjectView/>
                 <LoginView />*/}
                 <EditorView/>
                 
            </div>
        );
    }
}

export default App;
