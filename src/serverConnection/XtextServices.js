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

    static getProcessProposals() {
        TextEditorWrapper.editor.xtextServices.getProcessProposals();
    }

    /**
     * Creates an Entity and associates it to the specified target. If targetPath is undefined the created
     * entity will be associated to the root-node.
     *
     * @param entityDescription: CreateEntityDTO
     * @param targetPath: string
     *
     *      CreateEntityDTO: {
     *          name: string,
     *          className: string,
     *          value: string,
     *          children: CreateEntityDTO[]
     *      }
     */
    static createEntity(entityDescription, targetPath) {
        let creation = {
            entity: entityDescription,
            target: targetPath
        };
        TextEditorWrapper.editor.xtextServices.createEntity({createEntityDTO: JSON.stringify(creation)});
    }

    /**
     *
     * @param entityPath: string
     */
    static deleteEntity(entityPath) {
        TextEditorWrapper.editor.xtextServices.deleteEntity({deleteEntityDTO: JSON.stringify({entityPath: entityPath})});
    }

    /**
     *
     * @param sourceEntityPath: string
     * @param targetEntityPath: string
     */
    static createAssociation(sourceEntityPath, targetEntityPath) {
        let association = {
            source: sourceEntityPath,
            target: targetEntityPath
        };
        TextEditorWrapper.editor.xtextServices.createAssociation({associationDTO: JSON.stringify(association)});
    }

    /**
     *
     * @param sourceEntityPath: string
     * @param targetEntityPath: string
     */
    static deleteAssociation(sourceEntityPath, targetEntityPath) {
        let association = {
            source: sourceEntityPath,
            target: targetEntityPath
        };
        TextEditorWrapper.editor.xtextServices.deleteAssociation({associationDTO: JSON.stringify(association)});
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
     * Standard validation-service from text-editor
     */
    static validate() {
        TextEditorWrapper.editor.xtextServices.validate();
    }


    /**
     *
     * @param listener: function(serviceType: string, result: any)
     */
    static addSuccessListener(listener) {
        TextEditorWrapper.editor.xtextServices.successListeners.push(listener);
    }

    /**
     *
     * @param listener: function(serviceType: string, result: any)
     */
    static addErrorListener(listener) {
        TextEditorWrapper.editor.xtextServices.errorListeners.push(listener);
    }
}
