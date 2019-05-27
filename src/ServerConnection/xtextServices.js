

/***
 * (static) Service-Class for calling Xtext-Services from Xtext-Server
 *
 * Achtung:
 * Diese Klasse darf nur in Modulen verwendet werden, in denen Code deklarativ verwendet wird.
 * Wird bei einem Import eines Moduls Code ausgeführt, der eine der hier definierten Methoden
 * benutzt bevor die loadEditor.js fertig ist, ist window.editorViewer === undefined
 */
export default class XtextServices {

    static getEmfModel() {
        window.editorViewer.xtextServices.getJson();
    }

    static creatableEntityProposals() {
        window.editorViewer.xtextServices.creatableObjectProposals();
    }

    /**
     *
     * @param entity: {name: string, className: string}
     */
    static createEntity(entity) {
        window.editorViewer.xtextServices.createObject(entity);
    }

    /**
     *
     * @param entity: {name: string}
     */
    static deleteEntity(entity) {
        window.editorViewer.xtextServices.deleteEntity(entity.name);
    }

    /**
     *
     * @param entityFrom: {name: string, className: string}
     * @param entityTo: {name: string, className: string}
     */
    static createAssociation(entityFrom, entityTo) {
        let association = {
            fromName: entityFrom.name,
            fromClassName: entityFrom.className,
            toName: entityTo.name,
            toClassName: entityTo.className
        };
        window.editorViewer.xtextServices.associate(association);
    }

    /**
     *
     * @param entityFrom: {name: string, className: string}
     * @param entityTo: {name: string, className: string}
     */
    static deleteAssociation(entityFrom, entityTo) {
        /*
        let association = {
            fromName: entityFrom.name,
            fromClassName: entityFrom.className,
            toName: entityTo.name,
            toClassName: entityTo.className
        };
        */
        //TODO: add service
    }

    /**
     *
     * @param entities: [{name: string, className: string},...]
     */
    static getEntityAttributes(entities) {
        let classNames = entities.map((entity) => {
            return entity.className
        });
        window.editorViewer.xtextServices.getEntityAttributeDefinition(classNames);
    }

    /**
     *
     * @param setEntityAttributeDTOs:   [{
     *                                      name: string,
     *                                      className: string, //not used
     *                                      attributes: [{
     *                                          name: string,
     *                                          value: string
     *                                      },...]
     *                                  },...]
     */
    static setEntityAttributes(setEntityAttributeDTOs) {
        window.editorViewer.xtextServices.setEntityAttribute({classDTOs: setEntityAttributeDTOs});
    }



    /**
     *
     * @param listener: function(serviceType: string, result: any)
     */
    static addSuccessListener(listener) {
        window.editorViewer.xtextServices.successListeners.push(listener);
    }
}
