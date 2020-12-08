import ProcessCallLoadDataSet from './ProcessCallLoadDataSet';

export default {
    verify: (emfEntity) => {
        if (emfEntity.className !== "de.unibonn.simpleml.simpleML.ProcessCall")
            return false;
        if (emfEntity.ref === undefined)
            return false;
        if (emfEntity.ref !== 'loadDataset')
            return false;
        return true;
    },
    mxGraphMetadata: {
        component: ProcessCallLoadDataSet,
    },
    propsEditorComponent: {} // TODO
}