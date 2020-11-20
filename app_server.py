
import asyncio
import os
from time import sleep

from xmlrpc.client import DateTime

import websockets
import sys




async def hello(websocket, path):

    file_number = await websocket.recv()


    n = int(file_number)

    for i in range(n):
        await websocket.send("please send the name of file " + str(i+1))
        file_name=await websocket.recv()
        await websocket.send("please send content of " +file_name)
        file_content = await websocket.recv()

        f=open("runtime/"+file_name,"w+")
        f.write(file_content)
        f.close()
    await websocket.send("please send the name of the main file")
    main_file = await websocket.recv()

    log = open('runtime/' + main_file + '.log', 'a',1)
    sys.stdout = log
    import subprocess


    result = subprocess.check_output('python runtime/' +main_file, shell=True)
    print("Excuting file: "+main_file)
    print(result)

    log.flush()

    # print("ssss")
    log.close()






start_server = websockets.serve(hello, "localhost", 8765)

asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever()