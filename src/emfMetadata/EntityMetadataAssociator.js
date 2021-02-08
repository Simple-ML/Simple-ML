import GenericProcessCallMetadata from './GenericProcessCall/GenericProcessCall.metadata';
import ProcessCallLoadDataSetMetadata from './ProcessCallLoadDataSet/ProcessCallLoadDataSet.metadata';

import GenericDataSetMetadata from './GenericDataSet/GenericDataSet.metadata';

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
     
     * @param {*} rawEmfEntity                          entity-object from server
     */
    getMetadata = (rawEmfEntity) => {
        for (let metadata of this.metadataContainer) {
            try {
                if(metadata.verify(rawEmfEntity))
                    return metadata;
            } catch(e) {
                console.log(e)
            }
        }
        return undefined;
    };
}

const entityMetadataAssociator = new EntityMetadataAssociator();

// Add metadata-objects here. Put more specific metadata before less specific e.g.:
// wrong!
// entityMetadataAssociator.addMetadata(ProcessCall);
// entityMetadataAssociator.addMetadata(ProcessCallWithNameLoadData);
// correct!
// entityMetadataAssociator.addMetadata(ProcessCallWithNameLoadData);
// entityMetadataAssociator.addMetadata(ProcessCall);

entityMetadataAssociator.addMetadata(ProcessCallLoadDataSetMetadata);
entityMetadataAssociator.addMetadata(GenericProcessCallMetadata);
entityMetadataAssociator.addMetadata(GenericDataSetMetadata);

export default entityMetadataAssociator;