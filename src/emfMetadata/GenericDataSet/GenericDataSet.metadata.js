import GenericDataSet from './GenericDataSet';

export default {
    verify: (emfEntity) => {
        if (emfEntity.className !== "de.unibonn.simpleml.simpleML.Assignment")
            return false;

        return true;
    },
    mxGraphMetadata: {
        component: GenericDataSet,
    },
    propsEditorComponent: {} // TODO
}