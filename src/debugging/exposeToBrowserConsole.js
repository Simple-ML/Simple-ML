
import XtextServices from '../serverConnection/XtextServices';
import EmfModelHelper from '../helper/EmfModelHelper';
import DataServices from '../serverConnection/DataServices';
import { showModal } from '../reducers/modal';
import DefaultModal from '../components/core/Modal/DefaultModal';
import store from '../reduxStore';
import dataEndpoint from "../serverConnection/dataEndpoint";

const debugInterface = {
    x: { //xtext
        s: { //services
            getEmfModel: () => XtextServices.getEmfModel(),
            creatableEntityProposals: () => XtextServices.creatableEntityProposals(),
            createEntity: (entity) => XtextServices.createEntity(entity),
            deleteEntity: (entityPath) => XtextServices.deleteEntity(entityPath),
            createAssociation: (fromEntityPath, toEntityPath) => XtextServices.createAssociation(fromEntityPath, toEntityPath),
            deleteAssociation: (fromEntityPath, toEntityPath) => XtextServices.deleteAssociation(fromEntityPath, toEntityPath),
            getEntityAttributes: (entities) => XtextServices.getEntityAttributes(entities),
            setEntityAttributes: (entity) => XtextServices.setEntityAttributes(entity)
        }
    },
    h: { //helper
        flattenEmfModelTree: (emfModelTree) => EmfModelHelper.flattenEmfModelTree(emfModelTree),
        getFullHierarchy: (emfEntity) => EmfModelHelper.getFullHierarchy(emfEntity),
        getFullHierarchy2: (emfEntity) => EmfModelHelper.getFullHierarchy2(emfEntity)
    },
    o: { //other
        showDefaultModal: () => store.dispatch(showModal(DefaultModal, {text: 'some text', message: 'some message'}))
    },
    l3s: {
        createProject: () => DataServices.createProject(),
        viewDataSets: (id) => DataServices.viewDataSets(id)
    },
    d: { //data
        lsr: {}, //lastServiceResult
        emf: {},
        ce: { //createEntity
            name: 'x',
            className: 'Assignment',
            value: '',
            children: [{
                name: 'init',
                className: 'ProcessCall',
                value: '' ,
                children: [{
                    className: 'StringLiteral',
                    value: 'someText'
                },
                {
                    className: 'IntegerLiteral',
                    value: '23'
                }]
            }]
        }
    }
};

var exposeToBrowserConsole = () => {
    window.deb = debugInterface
};

export {
    debugInterface,
    exposeToBrowserConsole
}
