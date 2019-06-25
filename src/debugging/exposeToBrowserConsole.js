
import XtextServices from '../serverConnection/XtextServices';


var exposeToBrowserConsole = () => {
    window.deb = {
        x: {
            s: {
                getEmfModel: () => XtextServices.getEmfModel(),
                creatableEntityProposals: () => XtextServices.creatableEntityProposals(),
                createEntity: (entity) => XtextServices.createEntity(entity),
                deleteEntity: (entity) => XtextServices.deleteEntity(entity),
                createAssociation: (entityFrom, entityTo) => XtextServices.createAssociation(entityFrom, entityTo),
                deleteAssociation: (entityFrom, entityTo) => XtextServices.deleteAssociation(entityFrom, entityTo),
                getEntityAttributes: (entities) => XtextServices.getEntityAttributes(entities),
                setEntityAttributes: XtextServices.setEntityAttributes
            }
        }
    };
};

export default exposeToBrowserConsole
