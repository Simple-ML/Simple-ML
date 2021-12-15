import asyncio

import websockets

from appServer import requestHandler

if __name__ == "__main__":
    print('Runtime server started on localhost:6789')
    # codeWorker = CodeWorker(name=get_new_session())
    try:
        ws_server = websockets.serve(requestHandler, '0.0.0.0', 6789)
        loop = asyncio.get_event_loop()
        loop.run_until_complete(ws_server)
        loop.run_forever()
    except KeyboardInterrupt:
        stopFlag = True
        # TODO: close ws server and loop correctely
        print("Exiting program...")
    except Exception as exc:
        # log = open('runtime/error.log', 'a')
        # print("error: {0}\n".format(exc))
        # log.write("error: {0}\n".format(exc))
        pass
