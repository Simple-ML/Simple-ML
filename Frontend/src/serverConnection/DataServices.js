import dataEndpoint from './dataEndpoint';

/**
 * will be deprecated
 */
export default class DataServices {

    static createProject() {
        window.$.ajax({
            url: dataEndpoint.host + '/runOperation',
            method: 'POST',
            headers: {
                contentType: "application/x-www-form-urlencoded; charset=UTF-8"
            },
            data: JSON.stringify({
                "operation": "create_project",
                "options": {
                    "title": "test",
                    "description": "test"
                }
            }),
            success: function (data) {
                window.deb.d.l3s.projectId = data.result.project.id;
                console.log(data);
            },
            error: function (xhr, desc, err) {
                console.log("Desc: " + desc + "\nErr:" + err);
            }
        })
    }

    static getDataSets(projectId) {
        const tempProjectId = projectId ? projectId : window.deb.d.l3s.projectId
        window.$.ajax({
            url: dataEndpoint.host + '/projects/' + tempProjectId + '/runOperation',
            method: 'POST',
            headers: {
                contentType: "application/x-www-form-urlencoded; charset=UTF-8"
            },
            data: JSON.stringify({
                "operation": "view_datasets",
                "options": {}
            }),
            success: function (data) {
                console.log(data);
                window.deb.d.l3s.dataSets = data.result;
            },
            error: function (xhr, desc, err) {
                console.log("Desc: " + desc + "\nErr:" + err);
            }
        })
    }

    static getDataSetMetadata(projectId) {
        const tempProjectId = projectId ? projectId : window.deb.d.l3s.projectId
        window.$.ajax({
            url: dataEndpoint.host + '/projects/' + tempProjectId + '/datasets/XXX/runOperation',
            method: 'POST',
            headers: {
                contentType: "application/x-www-form-urlencoded; charset=UTF-8"
            },
            data: JSON.stringify({
                "operation": "view_dataset",
                "options": {}
            }),
            success: function (data) {
                console.log(data);
                window.deb.d.l3s.dataSet = data.result;
            },
            error: function (xhr, desc, err) {
                console.log("Desc: " + desc + "\nErr:" + err);
            }
        })
    }
}
