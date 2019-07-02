import TextEditorWrapper from '../components/EditorView/TextEditor/TextEditorWrapper'

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
        TextEditorWrapper.editor.xtextServices.getEmfModel();
    }

    static creatableEntityProposals() {
        TextEditorWrapper.editor.xtextServices.creatableObjectProposals();
    }

    /**
     *
     * @param entityDescription:  CreateEntityDTO
     *
     *      CreateEntityDTO: {
     *          name: string,
     *          className: string,
     *          children: CreateEntityDTO[]
     *      }
     */
    static createEntity(entityDescription) {
        TextEditorWrapper.editor.xtextServices.createEntity({createEntityDTO: entityDescription});
    }

    /**
     *
     * @param entityPath: string
     */
    static deleteEntity(entityPath) {
        TextEditorWrapper.editor.xtextServices.deleteEntity({entityPath});
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
        TextEditorWrapper.editor.xtextServices.associate(association);
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
        TextEditorWrapper.editor.xtextServices.getEntityAttributeDefinition({classNameArray: classNames});
    }

    /**
     *
     * @param setEntityAttributeDTOs:   [{
     *                                      name: string,
     *                                      className: string, //not use
     *                                      attributes: [{
     *                                          name: string,
     *                                          value: string
     *                                      },...]
     *                                  },...]
     */
    static setEntityAttributes(setEntityAttributeDTOs) {
        TextEditorWrapper.editor.xtextServices.setEntityAttribute({classDTOs: setEntityAttributeDTOs});
    }



    /**
     *
     * @param listener: function(serviceType: string, result: any)
     */
    static addSuccessListener(listener) {
        TextEditorWrapper.editor.xtextServices.successListeners.push(listener);
    }
}
