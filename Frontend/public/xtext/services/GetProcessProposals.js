
 define(['xtext/services/XtextService', 'jquery'], function(XtextService, jQuery) {

    /**
     * Service class for loading resources. The resulting text is passed to the editor context.
     */
    function GetProcessProposalsService(serviceUrl, resourceId) {
        this.initialize(serviceUrl, 'getProcessProposals', resourceId);
        this._completionCallbacks = [];
    };

    GetProcessProposalsService.prototype = new XtextService();

    GetProcessProposalsService.prototype.onComplete = function(xhr, textStatus) {
        var callbacks = this._completionCallbacks;
        this._completionCallbacks = [];
        for (var i = 0; i < callbacks.length; i++) {
            var callback = callbacks[i].callback;
            var params = callbacks[i].params;
            callback(params);
        }
    }

    /**
     * Add a callback to be invoked when the service call has completed.
     */
     GetProcessProposalsService.prototype.addCompletionCallback = function(callback, params) {
        this._completionCallbacks.push({callback: callback, params: params});
    }

    GetProcessProposalsService.prototype.invoke = function(editorContext, params, deferred) {
        if (deferred === undefined) {
            deferred = jQuery.Deferred();
        }
        var knownServerState = editorContext.getServerState();
        if (knownServerState.updateInProgress) {
            var self = this;
            this.addCompletionCallback(function() { self.invoke(editorContext, params, deferred) });
            return deferred.promise();
        }

        var serverData = {
            contentType: params.contentType,
            entityPath: params.entityPath,
            frontendId: params.entityId
        };

        knownServerState.updateInProgress = true;
        var self = this;
        self.sendRequest(editorContext, {
            type: 'POST',
            data: serverData,

            success: function(result) {
                editorContext.setText(result.fullText);
                editorContext.setDirty();
                var listeners = editorContext.updateServerState(result.fullText, result.stateId);
                for (var i = 0; i < listeners.length; i++) {
                    listeners[i](params);
                }
                deferred.resolve(result);
            },

            error: function(xhr, textStatus, errorThrown) {
                if (xhr.status == 404 && !params.loadFromServer && knownServerState.text !== undefined) {
                    // The server has lost its session state and the resource is not loaded from the server
                    delete knownServerState.updateInProgress;
                    delete knownServerState.text;
                    delete knownServerState.stateId;
                    self.invoke(editorContext, params, deferred);
                    return true;
                }
                deferred.reject(errorThrown);
            },

            complete: self.onComplete.bind(self)
        }, true);
        return deferred.promise().always(function() {
            knownServerState.updateInProgress = false;
        });
    };

    GetProcessProposalsService.prototype._getSuccessCallback = function(editorContext, params, deferred) {
        return function(result) {
            editorContext.setText(result.fullText);
            var listeners = editorContext.updateServerState(result.fullText, result.stateId);
            for (var i = 0; i < listeners.length; i++) {
                listeners[i](params);
            }
            deferred.resolve(result);
        }
    }

    return GetProcessProposalsService;
});