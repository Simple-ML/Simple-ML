
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
            data: {
                "operation": "create_project",
                "options": {
                    "title":"test",
                    "description":"test"
                }

            },
            error: function (xhr, desc, err) {
                console.log(xhr);
                console.log("Desc: " + desc + "\nErr:" + err);
            }
        })
    }

    static viewDataSets(projectId) {

    }

    static viewDataSet(projectId) {

    }
}
