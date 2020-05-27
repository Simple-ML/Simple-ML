import ProcessCall from "./ProcessCall";

export default {
    verify: (emfEntity) => {
        if (emfEntity.className !== "de.unibonn.simpleml.simpleML.ProcessCall")
            return false;

        return true;
    },
    mxGraphMetadata: {
        component: ProcessCall,
        
    },
    propsEditorComponent: {} // TODO
}