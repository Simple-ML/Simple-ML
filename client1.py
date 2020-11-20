import asyncio


import websockets

minus = 1
plus = 2
value = 5
users = "firas"

uri = "ws://127.0.0.1:6789/"
async with websockets.connect(uri) as websocket:

websocket = new WebSocket("ws://127.0.0.1:6789/");
minus.onclick = function(event)
{
    websocket.send(JSON.stringify({action: 'minus'}));
}
plus.onclick = function(event)
{
    websocket.send(JSON.stringify({action: 'plus'}));
}
websocket.onmessage = function(event)
{
    data = JSON.parse(event.data);
switch(data.type)
{
    case
'state': \
    value.textContent = data.value;
break;
case
'users':
users.textContent = (
        data.count.toString() + " user" +
        (data.count == 1 ? "": "s"));
break;
default:
console.error(
    "unsupported event", data);
}
};