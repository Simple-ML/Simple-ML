import asyncio
import json

import websockets



async def hello():
    uri = "ws://127.0.0.1:6789/"
    async with websockets.connect(uri) as websocket:
        await websocket.recv()
        placeholder_req='{"action":"status","sessionId": "91b94fa4-c116-4ebf-ae38-3a28525feca3"}'#'#'{"action": "get_placeholder","placeholder":{"sessionId":"b91920d7-97ab-4fcd-985b-e57805b7b453","name":"message"}}'
        await websocket.send(placeholder_req)
        print(placeholder_req)
        placeholder = await websocket.recv()
        print("placeHolder",placeholder)



asyncio.get_event_loop().run_until_complete(hello())

# async def getplaceHolder():
#     uri = "ws://localhost:8765"
#     async with websockets.connect(uri) as websocket:
#
# asyncio.get_event_loop().run_forever()