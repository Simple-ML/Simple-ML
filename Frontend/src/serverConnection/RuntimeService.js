import runtimeEndpoint from './runtimeEndpoint';
import store from '../reduxStore';
import { setSessionId, savePlaceholder } from '../reducers/runtime';
import { createChainedFunction } from '@material-ui/core';

export default class RuntimeService {

    static websocket = RuntimeService.createWebSocket();

    static createWebSocket() {
        return new Promise((resolve, reject) => {
            var websocket = new WebSocket(`${runtimeEndpoint.protocol}://${runtimeEndpoint.host}:${runtimeEndpoint.port}`);
            websocket.onopen = () => {
                resolve(websocket);
            };
            websocket.onmessage = RuntimeService.onMessage;
            websocket.onerror = (err) => {
                console.log('WebSocket', err);
                reject(err);
            };
        });
    }

    static onMessage(event) {
        var data = JSON.parse(event.data);
        console.log('RuntimeService');
        // because of bad api-design
        if(data.value && !data.type)
            data = JSON.parse(data.value);

        switch (data.type) {
            case '[status]:EXECUTION_STARTED':
                store.dispatch(setSessionId(data.sessionId));
                RuntimeService.saveSpeedAverage();
                break;
            case '[users]:INFO':
                break;
            case '[placeholder]:READY':
                RuntimeService.getPlaceholder(data.sessionId, data.name);
                break;
            case '[placeholder]:VALUE':
                store.dispatch(savePlaceholder(data.name, data.value));
                break;
            default:
                break;    
        }
    }

    /**
     * Sends Python-Files to runtime-server and triggers execution of this files.
     * 
     * @param artifacts:    [{ name: string, content: string }, ...]
     */
    static runWorkflow(artifacts) {
        RuntimeService.websocket.then((websocket) => {
    		websocket.send(JSON.stringify({
                action: 'run',
                python:{
                    number: artifacts.length,
                    files: artifacts.map((artifact) => {
                        return {
                            filename: artifact.name.split('/')[1],
                            content: artifact.content
                        };
                    }), 
                    mainfile: artifacts[1].name.split('/')[1]
                }
            })); 
	    }).catch((err) => {
            console.log('WS:RunWorkflow', err);
        });
    }

    /**
     * Get placeholder by name from runtime-server
     * @param name: String
     */
    static getPlaceholder(sessionId, name) {
        RuntimeService.websocket.then((websocket) => {
            websocket.send(JSON.stringify({
                action: 'get_placeholder',
                placeholder: {sessionId, name},
            }));
        }).catch((err) => {
            console.log('WS:getPlaceholder', err);
        });
    }

    /**
     * Get all datasets
     */
    static getAvailableDatasets(sessionId) {
        RuntimeService.websocket.then((websocket) => {
            websocket.send(JSON.stringify({
                action: 'get_available_dataset',
                placeholder: {sessionId},
            }));
        }).catch((err) => {
            console.log('WS:getAvailableDataset', err);
        });
    }

    static saveSpeedAverage() {
        const data = {
            "type": "dataset",
            "subjects": {
                "de": [
                    "Verkehr"
                ]
            },
            "title": "Durchschnittsgeschwindigkeit auf niedersächsischen Straßen",
            "description": "Durchschnittsgeschwindigkeit auf niedersächsischen Straßen in einem halbstündigen Intervall.",
            "null_string": "",
            "separator": ",",
            "fileName": "SpeedAverages.csv",
            "hasHeader": "false",
            "sample_instances": {
                "type": "table",
                "lines": [
                    [
                        558274771,
                        "primary",
                        null,
                        "2017-12-23 22:00:00",
                        "2017-12-23 22:30:00",
                        0,
                        0,
                        null,
                        "winter",
                        "DARK",
                        "True",
                        "Sat",
                        null
                    ],
                    [
                        295742846,
                        "motorway",
                        "100",
                        "2017-11-13 18:00:00",
                        "2017-11-13 18:30:00",
                        19,
                        11,
                        77.947,
                        "autumn",
                        "Dark",
                        "False",
                        "Mon",
                        null
                    ],
                    [
                        379745598,
                        "motorway",
                        "none",
                        "2017-08-17 23:30:00",
                        "2017-08-18 00:00:00",
                        9,
                        4,
                        87.889,
                        "summer",
                        "Dark",
                        "False",
                        "Thu",
                        null
                    ],
                    [
                        60209776,
                        "primary",
                        "40",
                        "2017-09-03 17:30:00",
                        "2017-09-03 18:00:00",
                        2,
                        1,
                        40.0,
                        "autumn",
                        "Daylight",
                        "True",
                        "Sun",
                        null
                    ],
                    [
                        270398472,
                        "motorway",
                        "none",
                        "2017-08-23 01:00:00",
                        "2017-08-23 01:30:00",
                        28,
                        15,
                        85.714,
                        "summer",
                        "Dark",
                        "False",
                        "Wed",
                        null
                    ],
                    [
                        331851089,
                        "motorway",
                        "none",
                        "2017-10-22 00:00:00",
                        "2017-10-22 00:30:00",
                        28,
                        2,
                        104.821,
                        "autumn",
                        "Dark",
                        "True",
                        "Sun",
                        null
                    ],
                    [
                        121964172,
                        "motorway",
                        "none",
                        "2017-12-16 05:00:00",
                        "2017-12-16 05:30:00",
                        14,
                        6,
                        84.643,
                        "winter",
                        "Dark",
                        "True",
                        "Sat",
                        null
                    ],
                    [
                        37055351,
                        "trunk",
                        "100",
                        "2017-08-12 18:00:00",
                        "2017-08-12 18:30:00",
                        2,
                        1,
                        100.5,
                        "summer",
                        "Daylight",
                        "True",
                        "Sat",
                        null
                    ],
                    [
                        333293282,
                        "motorway",
                        "none",
                        "2017-12-25 05:00:00",
                        "2017-12-25 05:30:00",
                        1,
                        1,
                        123.0,
                        "winter",
                        "Dark",
                        "False",
                        "Mon",
                        null
                    ],
                    [
                        34894613,
                        "motorway",
                        "signals",
                        "2017-11-11 20:30:00",
                        "2017-11-11 21:00:00",
                        6,
                        4,
                        117.333,
                        "autumn",
                        "Dark",
                        "True",
                        "Sat",
                        null
                    ]
                ],
                "header_labels": [
                    "Straßensegment (OpenStreetMap-ID)",
                    "Ortstyp (Bezeichnung)",
                    "Geschwindigkeitsbegrenzung (hat Geschwindigkeit)",
                    "Zeit (Zeit)",
                    "Zeit (Zeit)",
                    "Verkehrsfluss (Anzahl von Aufzeichnungen)",
                    "Verkehrsfluss (Fahrzeuganzahl)",
                    "Verkehrsfluss (Durchschnittsgeschwindigkeit)",
                    "Wetteraufzeichnung (Saison)",
                    "Wetteraufzeichnung (Tageslicht)",
                    "Zeit (am Wochenende)",
                    "Straßensegment (im Well-known-Binary-(WKB)-Format)"
                ],
                "data_types": [
                    "string",
                    "string",
                    "string",
                    "datetime",
                    "datetime",
                    "integer",
                    "integer",
                    "float",
                    "string",
                    "string",
                    "bool",
                    "spatial"
                ]
            },
            "id": "SpeedAverages",
            "number_of_instances": 1000,
            "attributes": {
                "osm_id": {
                    "label": "Straßensegment (OpenStreetMap-ID)",
                    "type": "string",
                    "simple_type": "string",
                    "id": "osm_id",
                    "statistics": {
                        "averageNumberOfCapitalisedValues": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.0,
                            "id": "averageNumberOfCapitalisedValues"
                        },
                        "averageNumberOfCharacters": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 8.59,
                            "id": "averageNumberOfCharacters"
                        },
                        "averageNumberOfDigits": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 8.59,
                            "id": "averageNumberOfDigits"
                        },
                        "averageNumberOfSpecialCharacters": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.0,
                            "id": "averageNumberOfSpecialCharacters"
                        },
                        "averageNumberOfTokens": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 1.0,
                            "id": "averageNumberOfTokens"
                        },
                        "numberOfDistinctValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 619,
                            "id": "numberOfDistinctValues"
                        },
                        "numberOfInvalidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfInvalidValues"
                        },
                        "numberOfNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfNullValues"
                        },
                        "numberOfValidNonNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidNonNullValues"
                        },
                        "numberOfValidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidValues"
                        },
                        "numberOfValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValues"
                        }
                    }
                },
                "street_type": {
                    "label": "Ortstyp (Bezeichnung)",
                    "type": "string",
                    "simple_type": "string",
                    "id": "street_type",
                    "statistics": {
                        "averageNumberOfCapitalisedValues": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.0,
                            "id": "averageNumberOfCapitalisedValues"
                        },
                        "averageNumberOfCharacters": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 8.004,
                            "id": "averageNumberOfCharacters"
                        },
                        "averageNumberOfDigits": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.0,
                            "id": "averageNumberOfDigits"
                        },
                        "averageNumberOfSpecialCharacters": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.008,
                            "id": "averageNumberOfSpecialCharacters"
                        },
                        "averageNumberOfTokens": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 1.0,
                            "id": "averageNumberOfTokens"
                        },
                        "numberOfDistinctValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 9,
                            "id": "numberOfDistinctValues"
                        },
                        "numberOfInvalidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfInvalidValues"
                        },
                        "numberOfNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfNullValues"
                        },
                        "numberOfValidNonNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidNonNullValues"
                        },
                        "numberOfValidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidValues"
                        },
                        "numberOfValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValues"
                        }
                    }
                },
                "max_speed": {
                    "label": "Geschwindigkeitsbegrenzung (hat Geschwindigkeit)",
                    "type": "string",
                    "simple_type": "string",
                    "id": "max_speed",
                    "statistics": {
                        "averageNumberOfCapitalisedValues": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.002,
                            "id": "averageNumberOfCapitalisedValues"
                        },
                        "averageNumberOfCharacters": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 3.996,
                            "id": "averageNumberOfCharacters"
                        },
                        "averageNumberOfDigits": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.829,
                            "id": "averageNumberOfDigits"
                        },
                        "averageNumberOfSpecialCharacters": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.0,
                            "id": "averageNumberOfSpecialCharacters"
                        },
                        "averageNumberOfTokens": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 1.0,
                            "id": "averageNumberOfTokens"
                        },
                        "numberOfDistinctValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 12,
                            "id": "numberOfDistinctValues"
                        },
                        "numberOfInvalidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 54,
                            "id": "numberOfInvalidValues"
                        },
                        "numberOfNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 54,
                            "id": "numberOfNullValues"
                        },
                        "numberOfValidNonNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 946,
                            "id": "numberOfValidNonNullValues"
                        },
                        "numberOfValidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidValues"
                        },
                        "numberOfValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValues"
                        }
                    }
                },
                "start_time": {
                    "label": "Zeit (Zeit)",
                    "type": "datetime",
                    "simple_type": "temporal",
                    "id": "start_time",
                    "statistics": {
                        "decile": {
                            "type": "list",
                            "id": "decile",
                            "list_data_type": "datetime",
                            "values": [
                                "31.07.2017, 23:00:00",
                                "14.08.2017, 12:27:00",
                                "27.08.2017, 22:30:00",
                                "11.09.2017, 15:21:00",
                                "28.09.2017, 11:36:00",
                                "14.10.2017, 00:30:00",
                                "01.11.2017, 00:12:00",
                                "15.11.2017, 05:51:00",
                                "29.11.2017, 16:12:00",
                                "16.12.2017, 04:03:00",
                                "31.12.2017, 15:30:00"
                            ]
                        },
                        "maximum": {
                            "type": "numeric",
                            "data_type": "datetime",
                            "value": "31.12.2017, 15:30:00",
                            "id": "maximum"
                        },
                        "mean": {
                            "type": "numeric",
                            "data_type": "datetime",
                            "value": "14.10.2017, 13:11:07",
                            "id": "mean"
                        },
                        "median": {
                            "type": "numeric",
                            "data_type": "datetime",
                            "value": "14.10.2017, 00:30:00",
                            "id": "median"
                        },
                        "minimum": {
                            "type": "numeric",
                            "data_type": "datetime",
                            "value": "31.07.2017, 23:00:00",
                            "id": "minimum"
                        },
                        "numberOfDistinctValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 924,
                            "id": "numberOfDistinctValues"
                        },
                        "numberOfInvalidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfInvalidValues"
                        },
                        "numberOfNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfNullValues"
                        },
                        "numberOfValidNonNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidNonNullValues"
                        },
                        "numberOfValidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidValues"
                        },
                        "numberOfValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValues"
                        },
                        "quartile": {
                            "type": "box_plot",
                            "id": "quartile",
                            "list_data_type": "datetime",
                            "values": [
                                "31.07.2017, 23:00:00",
                                "04.09.2017, 16:15:00",
                                "14.10.2017, 00:30:00",
                                "21.11.2017, 11:52:30",
                                "31.12.2017, 15:30:00"
                            ]
                        },
                        "histogram": {
                            "type": "histogram",
                            "bucket_data_type": "datetime",
                            "buckets": [
                                {
                                    "bucketMinimum": "31.07.2017, 23:00:00",
                                    "bucketMaximum": "16.08.2017, 05:27:00",
                                    "value": 109
                                },
                                {
                                    "bucketMinimum": "16.08.2017, 05:27:00",
                                    "bucketMaximum": "31.08.2017, 11:54:00",
                                    "value": 119
                                },
                                {
                                    "bucketMinimum": "31.08.2017, 11:54:00",
                                    "bucketMaximum": "15.09.2017, 18:21:00",
                                    "value": 89
                                },
                                {
                                    "bucketMinimum": "15.09.2017, 18:21:00",
                                    "bucketMaximum": "01.10.2017, 00:48:00",
                                    "value": 106
                                },
                                {
                                    "bucketMinimum": "01.10.2017, 00:48:00",
                                    "bucketMaximum": "16.10.2017, 07:15:00",
                                    "value": 92
                                },
                                {
                                    "bucketMinimum": "16.10.2017, 07:15:00",
                                    "bucketMaximum": "31.10.2017, 13:42:00",
                                    "value": 82
                                },
                                {
                                    "bucketMinimum": "31.10.2017, 13:42:00",
                                    "bucketMaximum": "15.11.2017, 20:09:00",
                                    "value": 106
                                },
                                {
                                    "bucketMinimum": "15.11.2017, 20:09:00",
                                    "bucketMaximum": "01.12.2017, 02:36:00",
                                    "value": 110
                                },
                                {
                                    "bucketMinimum": "01.12.2017, 02:36:00",
                                    "bucketMaximum": "16.12.2017, 09:03:00",
                                    "value": 90
                                },
                                {
                                    "bucketMinimum": "16.12.2017, 09:03:00",
                                    "bucketMaximum": "31.12.2017, 15:30:00",
                                    "value": 97
                                }
                            ]
                        }
                    }
                },
                "end_time": {
                    "label": "Zeit (Zeit)",
                    "type": "datetime",
                    "simple_type": "temporal",
                    "id": "end_time",
                    "statistics": {
                        "decile": {
                            "type": "list",
                            "id": "decile",
                            "list_data_type": "datetime",
                            "values": [
                                "31.07.2017, 23:30:00",
                                "14.08.2017, 12:57:00",
                                "27.08.2017, 23:00:00",
                                "11.09.2017, 15:51:00",
                                "28.09.2017, 12:06:00",
                                "14.10.2017, 01:00:00",
                                "01.11.2017, 00:42:00",
                                "15.11.2017, 06:21:00",
                                "29.11.2017, 16:42:00",
                                "16.12.2017, 04:33:00",
                                "31.12.2017, 16:00:00"
                            ]
                        },
                        "maximum": {
                            "type": "numeric",
                            "data_type": "datetime",
                            "value": "31.12.2017, 16:00:00",
                            "id": "maximum"
                        },
                        "mean": {
                            "type": "numeric",
                            "data_type": "datetime",
                            "value": "14.10.2017, 13:41:07",
                            "id": "mean"
                        },
                        "median": {
                            "type": "numeric",
                            "data_type": "datetime",
                            "value": "14.10.2017, 01:00:00",
                            "id": "median"
                        },
                        "minimum": {
                            "type": "numeric",
                            "data_type": "datetime",
                            "value": "31.07.2017, 23:30:00",
                            "id": "minimum"
                        },
                        "numberOfDistinctValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 924,
                            "id": "numberOfDistinctValues"
                        },
                        "numberOfInvalidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfInvalidValues"
                        },
                        "numberOfNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfNullValues"
                        },
                        "numberOfValidNonNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidNonNullValues"
                        },
                        "numberOfValidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidValues"
                        },
                        "numberOfValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValues"
                        },
                        "quartile": {
                            "type": "box_plot",
                            "id": "quartile",
                            "list_data_type": "datetime",
                            "values": [
                                "31.07.2017, 23:30:00",
                                "04.09.2017, 16:45:00",
                                "14.10.2017, 01:00:00",
                                "21.11.2017, 12:22:30",
                                "31.12.2017, 16:00:00"
                            ]
                        },
                        "histogram": {
                            "type": "histogram",
                            "bucket_data_type": "datetime",
                            "buckets": [
                                {
                                    "bucketMinimum": "31.07.2017, 23:30:00",
                                    "bucketMaximum": "16.08.2017, 05:57:00",
                                    "value": 109
                                },
                                {
                                    "bucketMinimum": "16.08.2017, 05:57:00",
                                    "bucketMaximum": "31.08.2017, 12:24:00",
                                    "value": 119
                                },
                                {
                                    "bucketMinimum": "31.08.2017, 12:24:00",
                                    "bucketMaximum": "15.09.2017, 18:51:00",
                                    "value": 89
                                },
                                {
                                    "bucketMinimum": "15.09.2017, 18:51:00",
                                    "bucketMaximum": "01.10.2017, 01:18:00",
                                    "value": 106
                                },
                                {
                                    "bucketMinimum": "01.10.2017, 01:18:00",
                                    "bucketMaximum": "16.10.2017, 07:45:00",
                                    "value": 92
                                },
                                {
                                    "bucketMinimum": "16.10.2017, 07:45:00",
                                    "bucketMaximum": "31.10.2017, 14:12:00",
                                    "value": 82
                                },
                                {
                                    "bucketMinimum": "31.10.2017, 14:12:00",
                                    "bucketMaximum": "15.11.2017, 20:39:00",
                                    "value": 106
                                },
                                {
                                    "bucketMinimum": "15.11.2017, 20:39:00",
                                    "bucketMaximum": "01.12.2017, 03:06:00",
                                    "value": 110
                                },
                                {
                                    "bucketMinimum": "01.12.2017, 03:06:00",
                                    "bucketMaximum": "16.12.2017, 09:33:00",
                                    "value": 90
                                },
                                {
                                    "bucketMinimum": "16.12.2017, 09:33:00",
                                    "bucketMaximum": "31.12.2017, 16:00:00",
                                    "value": 97
                                }
                            ]
                        }
                    }
                },
                "number_of_records": {
                    "label": "Verkehrsfluss (Anzahl von Aufzeichnungen)",
                    "type": "integer",
                    "simple_type": "numeric",
                    "id": "number_of_records",
                    "statistics": {
                        "averageNumberOfDigits": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 1.726,
                            "id": "averageNumberOfDigits"
                        },
                        "decile": {
                            "type": "list",
                            "id": "decile",
                            "list_data_type": "float",
                            "values": [
                                0.0,
                                4.0,
                                7.0,
                                10.0,
                                14.0,
                                17.0,
                                22.0,
                                29.0,
                                44.0,
                                233.0
                            ]
                        },
                        "maximum": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 233,
                            "id": "maximum"
                        },
                        "mean": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 19.986,
                            "id": "mean"
                        },
                        "median": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 14.0,
                            "id": "median"
                        },
                        "minimum": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "minimum"
                        },
                        "numberOfDistinctValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 98,
                            "id": "numberOfDistinctValues"
                        },
                        "numberOfInvalidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfInvalidValues"
                        },
                        "numberOfNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfNullValues"
                        },
                        "numberOfValidNonNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidNonNullValues"
                        },
                        "numberOfValidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidValues"
                        },
                        "numberOfValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValues"
                        },
                        "quartile": {
                            "type": "box_plot",
                            "id": "quartile",
                            "list_data_type": "float",
                            "values": [
                                0.0,
                                6.0,
                                14.0,
                                25.0,
                                233.0
                            ]
                        },
                        "standardDeviation": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 24.099,
                            "id": "standardDeviation"
                        },
                        "histogram": {
                            "type": "histogram",
                            "bucket_data_type": "float",
                            "buckets": [
                                {
                                    "bucketMinimum": 0.0,
                                    "bucketMaximum": 23.3,
                                    "value": 726
                                },
                                {
                                    "bucketMinimum": 23.3,
                                    "bucketMaximum": 46.6,
                                    "value": 187
                                },
                                {
                                    "bucketMinimum": 46.6,
                                    "bucketMaximum": 69.9,
                                    "value": 44
                                },
                                {
                                    "bucketMinimum": 69.9,
                                    "bucketMaximum": 93.2,
                                    "value": 23
                                },
                                {
                                    "bucketMinimum": 93.2,
                                    "bucketMaximum": 116.5,
                                    "value": 5
                                },
                                {
                                    "bucketMinimum": 116.5,
                                    "bucketMaximum": 139.8,
                                    "value": 6
                                },
                                {
                                    "bucketMinimum": 139.8,
                                    "bucketMaximum": 163.1,
                                    "value": 7
                                },
                                {
                                    "bucketMinimum": 163.1,
                                    "bucketMaximum": 186.4,
                                    "value": 1
                                },
                                {
                                    "bucketMinimum": 186.4,
                                    "bucketMaximum": 209.7,
                                    "value": 0
                                },
                                {
                                    "bucketMinimum": 209.7,
                                    "bucketMaximum": 233.0,
                                    "value": 1
                                }
                            ]
                        }
                    }
                },
                "number_of_drivers": {
                    "label": "Verkehrsfluss (Fahrzeuganzahl)",
                    "type": "integer",
                    "simple_type": "numeric",
                    "id": "number_of_drivers",
                    "statistics": {
                        "averageNumberOfDigits": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 1.439,
                            "id": "averageNumberOfDigits"
                        },
                        "decile": {
                            "type": "list",
                            "id": "decile",
                            "list_data_type": "float",
                            "values": [
                                0.0,
                                2.0,
                                4.0,
                                5.0,
                                7.0,
                                9.0,
                                12.0,
                                15.0,
                                19.0,
                                48.0
                            ]
                        },
                        "maximum": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 48,
                            "id": "maximum"
                        },
                        "mean": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 8.788,
                            "id": "mean"
                        },
                        "median": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 7.0,
                            "id": "median"
                        },
                        "minimum": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "minimum"
                        },
                        "numberOfDistinctValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 39,
                            "id": "numberOfDistinctValues"
                        },
                        "numberOfInvalidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfInvalidValues"
                        },
                        "numberOfNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfNullValues"
                        },
                        "numberOfValidNonNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidNonNullValues"
                        },
                        "numberOfValidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidValues"
                        },
                        "numberOfValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValues"
                        },
                        "quartile": {
                            "type": "box_plot",
                            "id": "quartile",
                            "list_data_type": "float",
                            "values": [
                                0.0,
                                3.0,
                                7.0,
                                13.0,
                                48.0
                            ]
                        },
                        "standardDeviation": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 7.498,
                            "id": "standardDeviation"
                        },
                        "histogram": {
                            "type": "histogram",
                            "bucket_data_type": "float",
                            "buckets": [
                                {
                                    "bucketMinimum": 0.0,
                                    "bucketMaximum": 4.8,
                                    "value": 346
                                },
                                {
                                    "bucketMinimum": 4.8,
                                    "bucketMaximum": 9.6,
                                    "value": 264
                                },
                                {
                                    "bucketMinimum": 9.6,
                                    "bucketMaximum": 14.4,
                                    "value": 187
                                },
                                {
                                    "bucketMinimum": 14.4,
                                    "bucketMaximum": 19.2,
                                    "value": 114
                                },
                                {
                                    "bucketMinimum": 19.2,
                                    "bucketMaximum": 24.0,
                                    "value": 45
                                },
                                {
                                    "bucketMinimum": 24.0,
                                    "bucketMaximum": 28.8,
                                    "value": 25
                                },
                                {
                                    "bucketMinimum": 28.8,
                                    "bucketMaximum": 33.6,
                                    "value": 10
                                },
                                {
                                    "bucketMinimum": 33.6,
                                    "bucketMaximum": 38.4,
                                    "value": 6
                                },
                                {
                                    "bucketMinimum": 38.4,
                                    "bucketMaximum": 43.2,
                                    "value": 2
                                },
                                {
                                    "bucketMinimum": 43.2,
                                    "bucketMaximum": 48.0,
                                    "value": 1
                                }
                            ]
                        }
                    }
                },
                "average_speed": {
                    "label": "Verkehrsfluss (Durchschnittsgeschwindigkeit)",
                    "type": "float",
                    "simple_type": "numeric",
                    "id": "average_speed",
                    "statistics": {
                        "averageNumberOfDigits": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 12.604,
                            "id": "averageNumberOfDigits"
                        },
                        "decile": {
                            "type": "list",
                            "id": "decile",
                            "list_data_type": "float",
                            "values": [
                                7.98,
                                68.527,
                                80.806,
                                85.37,
                                87.807,
                                90.726,
                                95.125,
                                99.0,
                                107.244,
                                116.025,
                                193.0
                            ]
                        },
                        "maximum": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 193.0,
                            "id": "maximum"
                        },
                        "mean": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 91.578,
                            "id": "mean"
                        },
                        "median": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 90.726,
                            "id": "median"
                        },
                        "minimum": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 7.98,
                            "id": "minimum"
                        },
                        "numberOfDistinctValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 814,
                            "id": "numberOfDistinctValues"
                        },
                        "numberOfInvalidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 111,
                            "id": "numberOfInvalidValues"
                        },
                        "numberOfNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 111,
                            "id": "numberOfNullValues"
                        },
                        "numberOfValidNonNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 889,
                            "id": "numberOfValidNonNullValues"
                        },
                        "numberOfValidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidValues"
                        },
                        "numberOfValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValues"
                        },
                        "quartile": {
                            "type": "box_plot",
                            "id": "quartile",
                            "list_data_type": "float",
                            "values": [
                                7.98,
                                83.688,
                                90.726,
                                102.745,
                                193.0
                            ]
                        },
                        "standardDeviation": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 22.632,
                            "id": "standardDeviation"
                        },
                        "histogram": {
                            "type": "histogram",
                            "bucket_data_type": "float",
                            "buckets": [
                                {
                                    "bucketMinimum": 7.98,
                                    "bucketMaximum": 26.482,
                                    "value": 17
                                },
                                {
                                    "bucketMinimum": 26.482,
                                    "bucketMaximum": 44.984,
                                    "value": 17
                                },
                                {
                                    "bucketMinimum": 44.984,
                                    "bucketMaximum": 63.486,
                                    "value": 36
                                },
                                {
                                    "bucketMinimum": 63.486,
                                    "bucketMaximum": 81.988,
                                    "value": 122
                                },
                                {
                                    "bucketMinimum": 81.988,
                                    "bucketMaximum": 100.49,
                                    "value": 445
                                },
                                {
                                    "bucketMinimum": 100.49,
                                    "bucketMaximum": 118.992,
                                    "value": 178
                                },
                                {
                                    "bucketMinimum": 118.992,
                                    "bucketMaximum": 137.494,
                                    "value": 58
                                },
                                {
                                    "bucketMinimum": 137.494,
                                    "bucketMaximum": 155.996,
                                    "value": 9
                                },
                                {
                                    "bucketMinimum": 155.996,
                                    "bucketMaximum": 174.498,
                                    "value": 4
                                },
                                {
                                    "bucketMinimum": 174.498,
                                    "bucketMaximum": 193.0,
                                    "value": 3
                                }
                            ]
                        }
                    }
                },
                "season": {
                    "label": "Wetteraufzeichnung (Saison)",
                    "type": "string",
                    "simple_type": "string",
                    "id": "season",
                    "statistics": {
                        "averageNumberOfCapitalisedValues": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.0,
                            "id": "averageNumberOfCapitalisedValues"
                        },
                        "averageNumberOfCharacters": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 6.0,
                            "id": "averageNumberOfCharacters"
                        },
                        "averageNumberOfDigits": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.0,
                            "id": "averageNumberOfDigits"
                        },
                        "averageNumberOfSpecialCharacters": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.0,
                            "id": "averageNumberOfSpecialCharacters"
                        },
                        "averageNumberOfTokens": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 1.0,
                            "id": "averageNumberOfTokens"
                        },
                        "numberOfDistinctValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 3,
                            "id": "numberOfDistinctValues"
                        },
                        "numberOfInvalidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfInvalidValues"
                        },
                        "numberOfNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfNullValues"
                        },
                        "numberOfValidNonNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidNonNullValues"
                        },
                        "numberOfValidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidValues"
                        },
                        "numberOfValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValues"
                        }
                    }
                },
                "daylight": {
                    "label": "Wetteraufzeichnung (Tageslicht)",
                    "type": "string",
                    "simple_type": "string",
                    "id": "daylight",
                    "statistics": {
                        "averageNumberOfCapitalisedValues": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.922,
                            "id": "averageNumberOfCapitalisedValues"
                        },
                        "averageNumberOfCharacters": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 5.944,
                            "id": "averageNumberOfCharacters"
                        },
                        "averageNumberOfDigits": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.0,
                            "id": "averageNumberOfDigits"
                        },
                        "averageNumberOfSpecialCharacters": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 0.0,
                            "id": "averageNumberOfSpecialCharacters"
                        },
                        "averageNumberOfTokens": {
                            "type": "numeric",
                            "data_type": "float",
                            "value": 1.0,
                            "id": "averageNumberOfTokens"
                        },
                        "numberOfDistinctValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 4,
                            "id": "numberOfDistinctValues"
                        },
                        "numberOfInvalidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfInvalidValues"
                        },
                        "numberOfNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfNullValues"
                        },
                        "numberOfValidNonNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidNonNullValues"
                        },
                        "numberOfValidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidValues"
                        },
                        "numberOfValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValues"
                        }
                    }
                },
                "is_weekend": {
                    "label": "Zeit (am Wochenende)",
                    "type": "bool",
                    "simple_type": "boolean",
                    "id": "is_weekend",
                    "statistics": {
                        "numberOfInvalidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfInvalidValues"
                        },
                        "numberOfNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfNullValues"
                        },
                        "numberOfValidNonNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidNonNullValues"
                        },
                        "numberOfValidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidValues"
                        },
                        "numberOfValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValues"
                        }
                    }
                },
                "geometry": {
                    "label": "Straßensegment (im Well-known-Binary-(WKB)-Format)",
                    "type": "geometry",
                    "simple_type": "geometry",
                    "id": "geometry",
                    "statistics": {
                        "numberOfInvalidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfInvalidValues"
                        },
                        "numberOfNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 0,
                            "id": "numberOfNullValues"
                        },
                        "numberOfValidNonNullValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidNonNullValues"
                        },
                        "numberOfValidValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValidValues"
                        },
                        "numberOfValues": {
                            "type": "numeric",
                            "data_type": "integer",
                            "value": 1000,
                            "id": "numberOfValues"
                        },
                        "spatialValueDistribution": {
                            "type": "spatial_value_distribution",
                            "areas": {
                                "area_de_ni_1": "10",
                                "area_de_ni_12": "48",
                                "area_de_ni_14": "89",
                                "area_de_ni_15": "8",
                                "area_de_ni_22": "30",
                                "area_de_ni_23": "33",
                                "area_de_ni_24": "12",
                                "area_de_ni_25": "35",
                                "area_de_ni_27": "1",
                                "area_de_ni_28": "9",
                                "area_de_ni_29": "45",
                                "area_de_ni_3": "31",
                                "area_de_ni_30": "12",
                                "area_de_ni_32": "48",
                                "area_de_ni_33": "35",
                                "area_de_ni_37": "6",
                                "area_de_ni_38": "2",
                                "area_de_ni_40": "29",
                                "area_de_ni_41": "44",
                                "area_de_ni_42": "21",
                                "area_de_ni_43": "8",
                                "area_de_ni_44": "17",
                                "area_de_ni_45": "48",
                                "area_de_ni_46": "22",
                                "area_de_ni_48": "47",
                                "area_de_ni_50": "1",
                                "area_de_ni_55": "3",
                                "area_de_ni_57": "1",
                                "area_de_ni_59": "15",
                                "area_de_ni_60": "141",
                                "area_de_ni_61": "40",
                                "area_de_ni_62": "6",
                                "area_de_ni_63": "29",
                                "area_de_ni_9": "5"
                            }
                        }
                    }
                }
            }
        };

        const jsonString = JSON.stringify(data);
        store.dispatch(savePlaceholder('speedAverages', {
            id: 'SpeedAverages',
            title: 'Durchschnittsgeschwindigkeit auf niedersächsischen Straßen',
            description: 'Durchschnittsgeschwindigkeit auf niedersächsischen Straßen in einem halbstündigen Intervall.',
            dataset_json: jsonString
        }));
    }
}