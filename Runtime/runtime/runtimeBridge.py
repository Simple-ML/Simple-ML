import asyncio
import copy
import json

import jsonpickle
import jsonpickle.ext.numpy as jsonpickle_numpy
import jsonpickle.ext.pandas as jsonpickle_pandas
import websockets


async def notify_server(placeHolder):
    """
        Notify the Runtime Server that a certain placeholder is ready

        placeholder: the placeholder wrapped in a json object
    """

    uri = "ws://localhost:6789"
    async with websockets.connect(uri) as websocket:
        await websocket.send(placeHolder)
        # await websocket.recv()


def save_placeholder(name, contents):
    """
        Save the placeholder when it is ready to be called from the code and
        name: name of the placeholder
        content: content of the placeholder to be saved
    """

    placeholder = dict()
    content = copy.deepcopy(contents)
    if type(content).__name__ == "ndarray":
        content = content.tolist()

    jsonpickle_pandas.register_handlers()
    jsonpickle_numpy.register_handlers()
    p = jsonpickle.pickler.Pickler()
    content = p.flatten(content)

    # elif type(content).__name__ == "Dataset":
    #     content = p.flatten(content)
    # elif type(content).__bases__[0].__name__ == "Estimator":
    #     content = content._underlying.__dict__
    # elif type(content).__bases__[0].__name__ == "Model":
    #     content = content._underlying.__dict__
    #     for k, v in content.items():
    #         if type(v).__name__=="int64":
    #             content[k] = int(v)
    #         elif isinstance(v, list) and v[-1:] == '_':
    #             content[k] = v.tolist()
    #         elif isinstance(v, numpy.ndarray):
    #             content[k] = v.tolist()
    #         elif type(v).__name__=="ndarray":
    #             content[k] = v.tolist()
    #         elif isinstance(v, Tree):
    #             # print(v.__doc__)
    #             # print(jsonpickle.encode(content))
    #
    #             # print(p.flatten(v))
    #             # import inspect
    #             # print(inspect.getmembers(v.__class__, lambda a:not(inspect.isroutine(a))))
    #             content[k] = "None"#p.flatten(v)
    #
    #         # print(type(v).__name__)

    # print(name,type(content).__name__)
    placeholder[name] = content

    # print(placeholder)
    # f = open("Placeholder.json", "a")
    # print(name)
    #
    # f.write(json.JSONEncoder().encode(placeholder))
    # f.close()
    loop = asyncio.new_event_loop()

    asyncio.set_event_loop(loop)
    # ser = False
    try:
        placeholder_value = json.JSONEncoder().encode({"action": 'placeholder_available', "placeholder": placeholder})
    #     ser=True
    except Exception as exc:
        print("exception", str(exc))
        placeholder_value = json.JSONEncoder().encode({"action": 'placeholder_available', "placeholder": placeholder})
    # if ser==False:
    #     try:
    #         placeholder_value=json.dumps({"action": 'placeholder_available',"placeholder":placeholder})
    #         ser=True
    #     except:
    #         x=placeholder._dataframe.to_json(orient="split")
    #         placeholder_value=json.JSONEncoder().encode({"action": 'placeholder_available',"placeholder":x})
    if name == "df_train":
        # print(content.__dict__)
        print(placeholder_value)

    asyncio.get_event_loop().run_until_complete(notify_server(placeholder_value))

# Test the functionaluty
# save_placeHolder("input data","""vehicletype,velocity,weekday,hour,highway,maxspeed
# 1,74,7,23,motorway_link,80
# 1,84,7,23,motorway,120
# 1,1,7,23,primary,50
# 1,86,7,23,motorway,none
# 1,83,7,23,motorway,signals
# 1,83,7,23,motorway,signals
# 1,10,7,23,?,?""")
