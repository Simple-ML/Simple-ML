import asyncio

import json
import websockets

"""
    Notify the Runtime Server that a certain placeholder is ready
    
    placeholder: the placeholder wrapped in a json object
"""
async def notify_server(placeHolder):
    uri = "ws://localhost:6789"
    async with websockets.connect(uri) as websocket:
        await websocket.send(placeHolder)


"""
    Save the placeholder when it is ready to be called from the code and 
    name: name of the placeholder
    content: content of the placeholder to be saved
"""
def save_placeHolder(name, content):
    placeholder = dict()
    # if type(content)=="ndarray":
    #     content=content.tolist()
    placeholder[name]=content
    f = open("Placeholder.json", "a")
    print(name)

    f.write(json.JSONEncoder().encode(placeholder))
    f.close()
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    asyncio.get_event_loop().run_until_complete(notify_server(json.JSONEncoder().encode({"action": 'placeholder_available',"placeholder":placeholder})))


# Test the functionaluty
# save_placeHolder("input data","""vehicletype,velocity,weekday,hour,highway,maxspeed
# 1,74,7,23,motorway_link,80
# 1,84,7,23,motorway,120
# 1,1,7,23,primary,50
# 1,86,7,23,motorway,none
# 1,83,7,23,motorway,signals
# 1,83,7,23,motorway,signals
# 1,10,7,23,?,?""")