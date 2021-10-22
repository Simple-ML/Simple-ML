
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
            getProcessMetadata: (entityPath) => XtextServices.getProcessMetadata(entityPath),
            getProcessProposals: (entityPath) => XtextServices.getProcessProposals(entityPath),
            createEntity: (entity) => XtextServices.createEntity(entity),
            deleteEntity: (entityPath) => XtextServices.deleteEntity(entityPath),
            createAssociation: (fromEntityPath, toEntityPath) => XtextServices.createAssociation(fromEntityPath, toEntityPath),
            deleteAssociation: (fromEntityPath, toEntityPath) => XtextServices.deleteAssociation(fromEntityPath, toEntityPath),
            getEntityAttributes: (entities) => XtextServices.getEntityAttributes(entities),
            setEntityAttributes: (entity) => XtextServices.setEntityAttributes(entity),
            generate: () => XtextServices.generate()
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
        getDataSetMetadata: (id) => DataServices.getDataSetMetadata(id),
        getDataSets: (id) => DataServices.getDataSets(id)

    },
    d: { //data
        lsr: {}, //lastServiceResult
        emf: {},
        emf_flat: {},
        emf_renderable: {},
        l3s: {
            projectId: '',
            dataSets: {},
            dataSet: {}
        },
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
        e2: { //createEntity
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

let exposeToBrowserConsole = () => {
    window.deb = debugInterface
};

export {
    debugInterface,
    exposeToBrowserConsole
}
