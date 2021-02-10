
export default class EmfModleVerificationHelper {

    static CONSTANTS = {
        SmlCall: 'de.unibonn.simpleml.simpleML.SmlCall',
        SmlReference: 'de.unibonn.simpleml.simpleML.SmlReference',
        SmlFunction: 'de.unibonn.simpleml.simpleML.SmlFunction',
        SmlPlaceholder: 'de.unibonn.simpleml.simpleML.SmlPlaceholder'
    }
    /**
     * Returns true if all attributes in 'verificationObject' are found in 'rawEmfEntity' (recursive). 
     * 
     * @param {*} rawEmfEntity                                  entity-object returns from server
     * @param {*} verificationObject 
     */
    static verifyRawEmfEntity(rawEmfEntity, verificationObject) {
        if (!rawEmfEntity || !verificationObject)
            return false;
        for (const objectKey in verificationObject) {
            if (!rawEmfEntity[objectKey])
                return false;
            if ((Array.isArray(rawEmfEntity[objectKey]) && Array.isArray(verificationObject[objectKey])) ||
                (typeof rawEmfEntity[objectKey] === 'object' && typeof verificationObject[objectKey] === 'object')) {
                    if (!EmfModleVerificationHelper.verifyRawEmfEntity(rawEmfEntity[objectKey], verificationObject[objectKey])) {
                        return false;
                    }
            } else if (rawEmfEntity[objectKey] !== verificationObject[objectKey])
                return false;
        }
        return true;
    }
}