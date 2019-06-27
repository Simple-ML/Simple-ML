
import XtextServices from '../serverConnection/XtextServices';


var debugInterface = {
    x: { //xtext
        s: { //services
            getEmfModel: () => XtextServices.getEmfModel(),
            creatableEntityProposals: () => XtextServices.creatableEntityProposals(),
            createEntity: (entity) => XtextServices.createEntity(entity),
            deleteEntity: (entity) => XtextServices.deleteEntity(entity),
            createAssociation: (entityFrom, entityTo) => XtextServices.createAssociation(entityFrom, entityTo),
            deleteAssociation: (entityFrom, entityTo) => XtextServices.deleteAssociation(entityFrom, entityTo),
            getEntityAttributes: (entities) => XtextServices.getEntityAttributes(entities),
            setEntityAttributes: (entity) => XtextServices.setEntityAttributes(entity)
        }
    },
    d: { //data
        lsr: {} //lastServiceResult
    }
};

var exposeToBrowserConsole = () => {
    window.deb = debugInterface
};

export {
    debugInterface,
    exposeToBrowserConsole
}
