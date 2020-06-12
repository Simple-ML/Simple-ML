import ProcessCallMetadata from './GenericProcessCall/GenericProcessCall.metadata';


/**
 * Associates metadata (for GUI-Elements) to an emfEntity
 */
class EntityMetadataAssociator {
    metadataContainer = [];

    /**
     * metadata: {
     *      verify: function(emfEntity)                 Returns true if metadata belongs to emfEntity.
     *      mxGraphMetadata: {
                component: MxGraphComponent             Derived class from MxGraphComponent.
     *      }, 
     *      propsEditorComponent: TODO                  Same as mxGraphComponent for PropsEditor
     *  }
     */
    addMetadata = (metadata) => {
        this.metadataContainer.push(metadata);
    };

    /**
     * Returns a metadata-object associated to the emfEntity
     */
    getMetadata = (emfEntity) => {
        for (let metadata of this.metadataContainer) {
            if(metadata.verify(emfEntity))
                return metadata
        }
        return undefined;
    };
}

const entityMetadataAssociator = new EntityMetadataAssociator();

// Add metadata-objects here. Put more specific metadata before less specific e.g.:
// correct!
// entityMetadataAssociator.addMetadata(ProcessCallWithNameLoadData);
// entityMetadataAssociator.addMetadata(ProcessCall);
// wrong!
// entityMetadataAssociator.addMetadata(ProcessCall);
// entityMetadataAssociator.addMetadata(ProcessCallWithNameLoadData);


entityMetadataAssociator.addMetadata(ProcessCallMetadata);

export default entityMetadataAssociator;