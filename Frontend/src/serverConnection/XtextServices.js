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


    /**
     * 
     * @param entityPathCollection: string[]
     */
    static getProcessMetadata(entityPathCollection) {
        TextEditorWrapper.editor.xtextServices.getProcessMetadata({entityPathCollection: JSON.stringify([...entityPathCollection])});
    }

    /**
     * 
     * @param {*} entityPath: string 
     */
    static getProcessProposals(entityId, entityPath) {
        TextEditorWrapper.editor.xtextServices.getProcessProposals({entityId: entityId, entityPath: entityPath});
    }

    /**
     * Creates an Entity and associates it to the specified target. If targetPath is undefined the created
     * entity will be associated to the root-node.
     *
     * @param createEntityDTO: CreateEntityDTO
     *
     *      CreateEntityDTO: {
     *          className: string,
     *          referenceIfFunktion: string,
     *          placeholderName: string,
     *          associationTargetPath: string
     *      }
     */
    static createEntity(createEntityDTO) {
        TextEditorWrapper.editor.xtextServices.createEntity({createEntityDTO: JSON.stringify(createEntityDTO)});
    }


    /**
     * 
     * @param {*} editProcessParameterDTO: EditProcessParameterDTO
     * 
     *      EditProcessParameterDTO: {
     *          entityPath: string,
     *          parameterType: string,
     *          parameterIndex: number,
     *          value: string
     * }
     */
    static editProcessParameter(editProcessParameterDTO) {
        TextEditorWrapper.editor.xtextServices.editProcessParameter({editProcessParameterDTO: JSON.stringify(editProcessParameterDTO)});
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
     * Generator Service
     */
    static generate() {
        TextEditorWrapper.editor.xtextServices.generate({allArtifacts: true});
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
