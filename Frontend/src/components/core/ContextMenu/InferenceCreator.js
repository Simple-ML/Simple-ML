


/**
 *
 *
 */
class InferenceCreator {

    inferenceContainer = {};
/*
    inferenceContainer['example'] = {
        verifyFunction = (context) => { return false },
        executeFunctions = [
            {
                metaData: {'name', 'icon', ...},  // data for ui-element (im most circumstances)
                func: (context) => {'do something'}
            }
        ]
    }
*/

    constructor() {
        this.addInference = this.addInference.bind(this);
        this.inferFromContext = this.inferFromContext.bind(this);
    }

    /**
     * Adds a executionFunction (for possible execution) to a verificationFunction listed in inferenceContainer.
     * If the verificationFunction is not listed the entry will be created. The key for the created entry in
     * inferenceContainer (hash-map) is the verificationFunction it self (as string)
     *
     * @param verifyFunction:   func(context:object):boolean    : Function for verification of context-object.
     * @param executeFunctions  func(context:object):void       : Function for possible execution
     *                                                            for given context-object
     */
    addInference = (verifyFunction, executeFunction, executeFunctionMetaData) => {
        if(typeof verifyFunction !== 'function' || typeof executeFunction !== 'function') {
            // TODO: real logging
            console.log({
                message: 'one of these is not a function',
                verifyFunc: verifyFunction,
                executeFunc: executeFunction
            })
        }

        if(this.inferenceContainer[''+verifyFunction] === undefined)
            this.inferenceContainer[''+verifyFunction] = {
                verifyFunction: this.wrapVerifyFunction(verifyFunction),
                executeFunctions: []
            };
        this.inferenceContainer[''+verifyFunction].executeFunctions.push({
            metaData: executeFunctionMetaData,
            func: executeFunction
        });
    }

    /**
     * Go through inferenceContainer and test (with verifyFunction) for fitting inference. Afterwards bind
     * context to inferred function and return all possible executable functions known for the context.
     *
     * @param context:object    : Can be anything (inferenceContainer[...].verifyFunction(context) has to return true)
     * @returns     [{
     *                  metaData: Object,
     *                  func: function
     *              },
     *              ...]
     */
    inferFromContext = (context) => {
        let result = [];

        Object.values(this.inferenceContainer).forEach((inference) => {
            if (inference.verifyFunction(context)) {
                inference.executeFunctions.forEach((executeFunction) => {
                    result.push({
                        metaData: executeFunction.metaData,
                        func: this.wrapExecuteFunctionAndBindContext(inference.verifyFunction, executeFunction.func, context)
                    })
                })
            }
        });
        return result;
    }

    wrapVerifyFunction = (verifyFunction) => {
        return (context) => {
            try {
                return verifyFunction(context);
            } catch(e) {
                // TODO: perhaps real logging
                console.log({
                    exception: e,
                    verifyFunc: verifyFunction,
                    context: context
                });
                // if exception is thrown the probability that the verificationFunction is not implemented
                // to verify this particular context is very high -> return false
                return false;
            }
        }
    }

    wrapExecuteFunctionAndBindContext = (verifyFunction, executeFunction, context) => {
        return () => {
            try {
                executeFunction(context)
            } catch(e) {
                //TODO: perhaps real logging
                console.log({
                    exception: e,
                    verifyFunc: verifyFunction,
                    executeFunc: executeFunction,
                    context: context
                })
            }
        }
    }
}

const inferenceCreator = new InferenceCreator();

export default inferenceCreator ;
