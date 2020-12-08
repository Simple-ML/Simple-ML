import GenericDataSet from './GenericDataSet';

export default {
    verify: (emfEntity) => {
        if (emfEntity.className !== "de.unibonn.simpleml.simpleML.SmlPlaceholder")
            return false;
        if (emfEntity['$ref'] !== undefined)
            return false;

        return true;
    },
    mxGraphMetadata: {
        component: GenericDataSet,
    },
    propsEditorComponent: {} // TODO
}