import runtimeEndpoint from './runtimeEndpoint';


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
        console.log(data)
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
                            filename: artifact.name,
                            content: artifact.content
                        };
                    }), 
                    mainfile: artifacts[0].name
                }
            })); 
            websocket.send(JSON.stringify({action: 'placeholder'}));
	    }).catch(function(err){});
    }
}