/*******************************************************************************
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

define(['xtext/services/XtextService', 'jquery'], function(XtextService, jQuery) {

    /**
     * Service class for loading resources. The resulting text is passed to the editor context.
     */
    function GetJsonService(serviceUrl, resourceId) {
        this.initialize(serviceUrl, 'json', resourceId);
    };

    GetJsonService.prototype = new XtextService();

    GetJsonService.prototype._initServerData = function(serverData, editorContext, params) {
        return {
            suppressContent: true,
            httpMethod: 'GET'
        };
    };

    GetJsonService.prototype._getSuccessCallback = function(editorContext, params, deferred) {
        return function(result) {

            var listeners = editorContext.updateServerState(result.fullText, result.stateId);
            for (var i = 0; i < listeners.length; i++) {
                listeners[i](params);
            }
            deferred.resolve(result);
        }
    }

    return GetJsonService;
});
