import GenericProcessCall from './GenericProcessCall';

export default {
    verify: (emfEntity) => {
        if (emfEntity.className !== "de.unibonn.simpleml.simpleML.SmlCall")
            return false;

        return true;
    },
    mxGraphMetadata: {
        component: GenericProcessCall,
    },
    propsEditorComponent: {} // TODO
}