import React from 'react';
import { connect } from 'react-redux';
import './App.scss';
import './componentCss.css';
import EditorView from './components/EditorView/EditorView';
import LoginView from './components/LoginView/LoginView';
import ChooseProjectView from './components/ChooseProjectView/ChooseProjectView';
import Toolbar from './components/core/Toolbar/Toolbar';
import ModalContainer from './components/core/Modal/ModalContainer';
import GraphicalEditor from './components/EditorView/GraphicalEditor/GraphicalEditor'
function connectToRuntime() {
    return new Promise(function(resolve, reject) {
				var websocket = new WebSocket("ws://127.0.0.1:6789/");
        websocket.onopen = function() {
            resolve(websocket);
        };
        websocket.onerror = function(err) {
            reject(err);
        };
    });
}

const handler = () => {
	var content1=document.querySelector('#file1');
	var content2=document.querySelector('#file2');
	connectToRuntime().then(function(websocket){
				websocket.onmessage = function (event) {
						var data = JSON.parse(event.data);
						console.log(data)
				};
			websocket.send(JSON.stringify({action: 'run',"python":{number:2,files:[{filename:'speedmain.py',content:content1.value}, {filename:'speedPrediction2.py',content:content2.value}], mainfile:'speedmain.py'}})); websocket.send(JSON.stringify({action: 'placeholder'}));
	}).catch(function(err){});

}
class App extends React.Component {
    render() {
        return (
            <div className="App">
						<textarea id="file1"></textarea>
						<textarea id="file2"></textarea>
						<button onClick={handler}/>
                <EditorView/>

                <Toolbar/>
                <ModalContainer/>
            </div>
        );
    }
}

export default connect()(App);
