
import XtextServices from '../serverConnection/XtextServices';
import EmfModelHelper from "../helper/EmfModelHelper";
import store from '../reduxStore';
import { showModal } from '../reducers/modal';
import DefaultModal from '../components/core/Modal/DefaultModal';

var debugInterface = {
    x: { //xtext
        s: { //services
            getEmfModel: () => XtextServices.getEmfModel(),
            creatableEntityProposals: () => XtextServices.creatableEntityProposals(),
            createEntity: (entity, targetPath) => XtextServices.createEntity(entity, targetPath),
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
    d: { //data
        lsr: {}, //lastServiceResult
        emf: {},
        e1: { //createEntity
            name: 'test',
            className: 'Assignment',
            value: '',
            children: [{
                name: 'project',
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
        },
        e2: {
            name: 'project',
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
