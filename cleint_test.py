import asyncio


import websockets



async def hello():
    uri = "ws://localhost:8765"
    async with websockets.connect(uri) as websocket:


        dict={}
        dict["speedmain.py"]=(open("speedmain.py").read())
        dict["speedPrediction2.py"]=(open("speedPrediction2.py").read())
        print(list(dict.keys()))
        number=str(len(dict))
        print(type(number))
        await websocket.send(number)




        for i in range(len(dict)):
            greeting = await websocket.recv()
            print(greeting)
            await websocket.send(list(dict.keys())[i])
            greeting = await websocket.recv()
            print(greeting)
            print("sending contents")
            # print(dict[list(dict.keys())[i]])
            await websocket.send(dict[list(dict.keys())[i]])
            print("in for")
        greeting = await websocket.recv()
        print(greeting)
        await websocket.send(list(dict.keys())[0])
        print("sent main file")

asyncio.get_event_loop().run_until_complete(hello())

# async def getplaceHolder():
#     uri = "ws://localhost:8765"
#     async with websockets.connect(uri) as websocket:
#
# asyncio.get_event_loop().run_forever()