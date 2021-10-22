
define(['xtext/services/XtextService', 'jquery'], function(XtextService, jQuery) {

    /**
     * Service class for loading resources. The resulting text is passed to the editor context.
     */
    function GetEmfModelService(serviceUrl, resourceId) {
        this.initialize(serviceUrl, 'getEmfModel', resourceId);
    };

    GetEmfModelService.prototype = new XtextService();

    GetEmfModelService.prototype._initServerData = function(serverData, editorContext, params) {
        return {
            suppressContent: true,
            httpMethod: 'GET'
        };
    };

    GetEmfModelService.prototype._getSuccessCallback = function(editorContext, params, deferred) {
        return function(result) {
            deferred.resolve(result);
        }
    };

    return GetEmfModelService;
});
