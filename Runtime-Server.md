Runtime server documentation

# Description

The Runtime server is the backend for running the generated python code. 
It is a WebSocket server that runs in the backend and cant be accessed directly by the end-User.

# Run

Run the following command in the root directory of the project to run the server and let it listen to port 8765

```
python app_server.py
```
Once the server is running then we would need to send requests and get back the results.

# Testing

Run the example clinet.html to see a demo of how the commands work

The client sending the request to the server should open a websocket as follows:
``` 
websocket = new WebSocket("ws://127.0.0.1:6789/");
```
**Note:** each request should be sent via a JSON String which contains the action required and the required attributes.
An example:
```
{action: 'Command',"Required Attribute":{Required Value}}
```
Following we present the list of commands available and what values should be given:

# List of the available request to the Runtime service

## Asynchronous run of the generated Code


* action:  run 

* Required Attribute:  python 
  
  * Required Value: A dictionary containing:
    * Number: number of files to be processed
    * files: list of file names and content 
    * mainfile: the name of the main file

Example:
```
{action: 'run',"python":{number:2,files:[{filename:'speedmain.py',content:content1.value},
                {filename:'speedPrediction2.py',content:content2.value}], mainfile:'speedmain.py'}}
```
## Get a saved PlaceHolder 


* action:  get_placeholder 

* Required Attribute: placeholder
  
  * Required Value: A dictionary containing:
    * sessionId: session Id of the run did
    * name: name of the placeholder
 

```
{action: 'get_placeholder',"placeholder":{sessionId:123123123123,name:"message"}}

```
## Run Model 


* action:  run_model 

* Required Attribute: model
  
  * Required Value: A dictionary containing:
    * sessionId: session Id of the run done
    * name: name of the model
    * is_data : get the data or a filepath
    * data: new instance that the model is required to predict

```
```
## Check status 


* action:  status 

* Required Attribute: sessionId
  
  * Required Value: the value of session ID

```
```
## Stop Run 

**Not yet available**

* action:  stop 

* Required Attribute: sessionId
  
  * Required Value: the value of session ID

Other requests to be developed and more info about the processes backend communication you can see it in [here](https://uni-bonn.sciebo.de/apps/onlyoffice/1336403918?filePath=%2Fshared_folder%2FWork%2FGesamtkonzept%2FArchitecture%2FRuntime%20API%20v0.1.xlsx)