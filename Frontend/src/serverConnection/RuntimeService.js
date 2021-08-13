import runtimeEndpoint from './runtimeEndpoint';
import store from '../reduxStore';
import { setSessionId, savePlaceholder } from '../reducers/runtime';
import { createChainedFunction } from '@material-ui/core';

export default class RuntimeService {

    static websocket = RuntimeService.createWebSocket();

    static createWebSocket() {
        return new Promise((resolve, reject) => {
            var websocket = new WebSocket(`${runtimeEndpoint.protocol}://${runtimeEndpoint.host}:${runtimeEndpoint.port}`);
            websocket.onopen = () => {
                resolve(websocket);
            };
            websocket.onmessage = RuntimeService.onMessage;
            websocket.onerror = (err) => {
                console.log('WebSocket', err);
                reject(err);
            };
        });
    }

    static onMessage(event) {
        var data = JSON.parse(event.data);
        
        // because of bad api-design
        if(data.value && !data.type)
            data = JSON.parse(data.value);

        switch (data.type) {
            case '[status]:EXECUTION_STARTED':
                store.dispatch(setSessionId(data.sessionId));
                break;
            case '[users]:INFO':
                break;
            case '[placeholder]:READY':
                RuntimeService.getPlaceholder(data.sessionId, data.name);
                break;
            case '[placeholder]:VALUE':
                store.dispatch(savePlaceholder(data.name, data.value));
                break;
            default:
                break;    
        }
    }

    /**
     * Sends Python-Files to runtime-server and triggers execution of this files.
     * 
     * @param artifacts:    [{ name: string, content: string }, ...]
     */
    static runWorkflow(artifacts) {
        RuntimeService.websocket.then((websocket) => {
    		websocket.send(JSON.stringify({
                action: 'run',
                python:{
                    number: artifacts.length,
                    files: artifacts.map((artifact) => {
                        return {
                            filename: artifact.name.split('/')[1],
                            content: artifact.content
                        };
                    }), 
                    mainfile: artifacts[1].name.split('/')[1]
                }
            })); 
	    }).catch((err) => {
            console.log('WS:RunWorkflow', err);
        });
    }

    /**
     * Get placeholder by name from runtime-server
     * @param name: String
     */
    static getPlaceholder(sessionId, name) {
        RuntimeService.websocket.then((websocket) => {
            websocket.send(JSON.stringify({
                action: 'get_placeholder',
                placeholder: {sessionId, name},
            }));
        }).catch((err) => {
            console.log('WS:getPlaceholder', err);
        });
    }
}