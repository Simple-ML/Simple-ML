
const http = require('http');

function createProject() {
    const data = JSON.stringify({
        "operation": "create_project",
        "options": {
            "title": "test",
            "description": "test"
        }
    });
    const createReq = http.request({
            hostname: 'smldapi.l3s.uni-hannover.de',
            path: '/runOperation',
            port: 80,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Content-Length': data.length
            }
        },
        (res) => {
            res.on('data', (d) => {
                console.log('createProject:\n');
                generateRequest(JSON.parse(d));
            });
        });
    createReq.on('error', (err) => {
        console.log(err);
    });
    createReq.write(data);
    createReq.end();
}


//--------------------------//--------------------------//--------------------------


function generateRequest(context) {
    const data = JSON.stringify({
        "operation": "generate_dataset",
        "options": {}
    });
    const generateReq = http.request({
            hostname: 'smldapi.l3s.uni-hannover.de',
            path: '/projects/' + context.result.project.id + '/datasets/ADACAugust/runOperation',
            port: 80,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Content-Length': data.length
            }
        },
        (res) => {
            res.on('data', (d) => {
                console.log('generateRequest:\n');
                sampleRequest({
                    r1: context,
                    r2: JSON.parse(d)
                });
            });
        });
    generateReq.on('error', (err) => {
        console.log(err);
    });
    generateReq.write(data);
    generateReq.end();
}


//--------------------------//--------------------------//--------------------------


function keepAttributeRequest(context) {
    const keepAttReq = http.request({
            hostname: 'http://smldapi.l3s.uni-hannover.de/projects/' + context.r1.result.project.id + '/datasets/' + context.r2.result.in_use_id + '/runOperation',
            method: 'POST',
            data: {
                "operation": "keep_attributes",
                "options": {
                    "attribute_ids": "1,2,3,4,5,6,7,8,9,10"
                }
            }
        },
        (res) => {
            res.on('data', (d) => {
      //          keepAttData = d;
            });
        });
    keepAttReq.on('error', (err) => {
        console.log(err);
    });
    keepAttReq.end();
}


//--------------------------//--------------------------//--------------------------


function sampleRequest(context) {
    const data = JSON.stringify({
        "operation": "sample",
        "options": {
            "method":"FIRST_N",
            "number_of_instances": 200
        }
    });
    const sampleReq = http.request({
            hostname: 'smldapi.l3s.uni-hannover.de',
            path: '/projects/' + context.r1.result.project.id + '/datasets/' + context.r2.result.in_use_id + '/runOperation',
            port: 80,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Content-Length': data.length
            }
        },
        (res) => {
            res.on('data', (d) => {
                console.log('sampleRequest:\n');
                materialiseRequest({
                    r1: context.r1,
                    r2: JSON.parse(d)

                });
            });
        });
    sampleReq.on('error', (err) => {
        console.log(err);
    });
    sampleReq.write(data);
    sampleReq.end();
}


//--------------------------//--------------------------//--------------------------


function materialiseRequest(context) {
    const data = JSON.stringify({
        "operation": "materialise",
        "options": {
        }
    });
    const materialiseReq = http.request({
            hostname: 'smldapi.l3s.uni-hannover.de',
            path: '/projects/' + context.r1.result.project.id + '/datasets/' + context.r2.result.in_use_id + '/runOperation',
            port: 80,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Content-Length': data.length
            }
        },
        (res) => {
            res.on('data', (d) => {
                console.log('materialiseRequest:\n');
                console.log(JSON.parse(d));
            });
        });
    materialiseReq.on('error', (err) => {
        console.log(err);
    });
    materialiseReq.write(data);
    materialiseReq.end();
}

function writeIntoFile(data) {
    const fs = require('fs');

    fs.writeFile("/L3SapiData", JSON.stringify(data), function(err) {

        if(err) {
            return console.log(err);
        }

        console.log("The file was saved!");
    });
}

createProject();
