#!/usr/bin/env python

# WS server example that synchronizes state across clients

import asyncio
import json
import logging
import websockets
import sys
import uuid
import threading
from simpleml.dataset._stats import getStatistics
from matplotlib.font_manager import json_load
import jsonpickle
import jsonpickle.ext.numpy as jsonpickle_numpy
import jsonpickle.ext.pandas as jsonpickle_pandas

logging.basicConfig()
jsonpickle_numpy.register_handlers()
jsonpickle_pandas.register_handlers()
jsonpickler = jsonpickle.pickler.Pickler()
unpickler = jsonpickle.unpickler.Unpickler()

STATE = {"value": 0}
MODEL=None
USERS = set()
RUNS= dict()
PlaceholderMap = dict()
SESSION = set()


def state_event():
    return json.dumps({ **STATE})


def users_event():
    return json.dumps({"type": "[users]:INFO", "count": len(USERS)})

async def notify_placeholder(message):
    print("notifying IDE")
    try:
        if USERS:  # asyncio.wait doesn't accept an empty list
            await asyncio.wait([user.send(message) for user in USERS])
    except:
        pass


async def notify_state():

    if USERS:  # asyncio.wait doesn't accept an empty list
        message = state_event()
        print(message)
        await asyncio.wait([user.send(message) for user in USERS])


async def notify_users():
    if USERS:  # asyncio.wait doesn't accept an empty list
        message = users_event()
        await asyncio.wait([user.send(message) for user in USERS])


async def register(websocket):
    USERS.add(websocket)
    await notify_users()


async def unregister(websocket):
    USERS.remove(websocket)
    await notify_users()


async def requestHandler(websocket, path):
    # register(websocket) sends user_event() to websocket
    await register(websocket)
    try:
        # await websocket.send(state_event())
        async for message in websocket:
            # print(message)
            data = json.loads(message)
            # print(data)
            # print(type(data))
            # print(data["action"])


            if data["action"] == "run":
                run_session=get_new_session()
                SESSION.add(run_session)
                PlaceholderMap[run_session] = dict()
                worker= CodeWorker(name=run_session,kwargs=data["python"])
                RUNS[run_session]=worker

                worker.start()

                STATE["value"] = json.JSONEncoder().encode({"type":"[status]:EXECUTION_STARTED","sessionId":run_session,"message":"Execution started"})
                await notify_state()
            elif data["action"] == "get_placeholder":
                # {action: 'get_placeholder',"placeholder":{sessionId:123123123123,name:"message"}}
                try:

                    if data["placeholder"]["sessionId"] in PlaceholderMap:

                        if type(PlaceholderMap[data["placeholder"]["sessionId"]][data["placeholder"]["name"]]).__name__=="Dataset":
                            placeholder_value=PlaceholderMap[data["placeholder"]["sessionId"]][data["placeholder"]["name"]].toArray().tolist()
                            placeholder_value = jsonpickler.flatten(placeholder_value)
                        else:
                            placeholder_value=jsonpickler.flatten(PlaceholderMap[data["placeholder"]["sessionId"]][data["placeholder"]["name"]])
                        await notify_placeholder(json.dumps({"type": "[placeholder]:VALUE", "sessionId": data["placeholder"]["sessionId"],
                                                             "description": "Placeholder_Value",
                                                             "name": data["placeholder"]["name"],
                                                             "value":placeholder_value
                                                             }))
                    else:
                        STATE["value"] = json.JSONEncoder().encode(
                        {"status": "Session not found"})
                        await notify_state()
                except Exception as exc:
                    print("error: {0}\n".format(exc))
                    log = open('runtime/error.log', 'a')
                    log.write("error: {0}".format(exc))
            elif data["action"] == "use_model":
                if MODEL==None:
                    print("")
                else:
                    MODEL.fit(data["data"])
            elif data["action"] == "get_stats":
                dataset=PlaceholderMap[data["placeholder"]["sessionId"]][data["placeholder"]["name"]]
                stats=getStatistics(dataset)

                await notify_placeholder(json.dumps({"type": "[placeholder]:STATS", "sessionId": data["placeholder"]["sessionId"],
                                                     "description": "Stats of a placeholder dataset",
                                                     "dataset_name": data["placeholder"]["name"],
                                                     "value":stats
                                                     }))
                pass
            elif data["action"] == "placeholder_available":
                print ("place holder available")
                placeholder = dict()
                placeholder_conetnt = unpickler.restore(data["placeholder"][list(data["placeholder"].keys())[0]])
                placeholder[list(data["placeholder"].keys())[0]]=placeholder_conetnt
                print(type(placeholder_conetnt))
                # print(data["placeholder"])
                # placeholder=data["placeholder"]
                if len(SESSION)>0:
                    current_session=SESSION.pop()
                else:
                    current_session = "1234"
                    PlaceholderMap[current_session]=dict()
                PlaceholderMap[current_session].update(placeholder)
                SESSION.add(current_session)

                await notify_placeholder(json.dumps({"type":"[placeholder]:READY","sessionId":current_session,"description":"Placeholder_available","name":list(data["placeholder"].keys())[0]}))
            elif data["action"] == "status":
                if data["sessionId"] in RUNS:
                    if RUNS[data["sessionId"]].is_alive():
                        STATE["value"] = json.JSONEncoder().encode(
                            { "status": "Alive"})
                        await notify_state()
                    else:
                        STATE["value"] = json.JSONEncoder().encode(
                            {"status": "Finished"})
                        await notify_state()
                else:
                    STATE["value"] = json.JSONEncoder().encode(
                        {"status": "Session not found"})
                    await notify_state()
            else:
                logging.error("unsupported event: {}", data)
    except ValueError as exc:
        print("exception")
        log = open('runtime/error.log', 'w')
        print("error: {0}\n in line {1}".format(exc,exc.args))
        log.write("error: {0}".format(exc))
        pass
    finally:
        await unregister(websocket)


# start_server = websockets.serve(counter, "localhost", 6789)
#
# asyncio.get_event_loop().run_until_complete(start_server)
# asyncio.get_event_loop().run_forever()

class CodeWorker(threading.Thread):

    def __init__(self, group=None, target=None, name=None,
                 args=(), kwargs=None, verbose=None):
        threading.Thread.__init__(self, group=group, target=target, name=name)
        self.args = args
        self.kwargs = kwargs
        return

    async def done(self):
        current_session = SESSION.pop()

        SESSION.add(current_session)
        await asyncio.wait([user.send(json.dumps({"type": "[state]:EXECUTION_FINISHED","sessionId":current_session ,**{"Description":"execution finished all placeholders should be ready "}})) for user in USERS])



    def run(self):

        file_number = self.kwargs["number"]
        n = int(file_number)
        for i in range(n):
            # await websocket.send("please send the name of file " + str(i + 1))
            file_name = self.kwargs["files"][i]["filename"]
            file_content = self.kwargs["files"][i]["content"]
            f = open("runtime/" + file_name, "w+")
            f.write(file_content)
            f.close()
        main_file = self.kwargs["mainfile"]
        log = open('runtime/' + main_file + '.log', 'a', 1)
        # sys.stdout = log
        import subprocess

        result = subprocess.check_output('python3.8 runtime/' + main_file, shell=True)
        print("Excuting file: " + main_file)
        # print(result)

        log.flush()

        # log.close()/
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        asyncio.get_event_loop().run_until_complete(self.done())
        # asyncio.run_coroutine_threadsafe(self.done(),asyncio.get_running_loop())
        logging.debug('running with %s and %s', self.args, self.kwargs)
        return


def get_new_session():
    return str(uuid.uuid4())



if __name__ == "__main__":
    print('Runtime server started')
    # codeWorker = CodeWorker(name=get_new_session())
    try:
        ws_server = websockets.serve(requestHandler, '0.0.0.0', 6789)
        loop = asyncio.get_event_loop()
        loop.run_until_complete(ws_server)
        loop.run_forever()
    except KeyboardInterrupt:
        stopFlag = True
        #TODO: close ws server and loop correctely
        print("Exiting program...")
    except Exception as exc:
        # log = open('runtime/error.log', 'a')
        # print("error: {0}\n".format(exc))
        # log.write("error: {0}\n".format(exc))
        pass
