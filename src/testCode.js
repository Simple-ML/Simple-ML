import XtextServices from "./ServerConnection/xtextServices";


let testCode = () => {
    XtextServices.addSuccessListener((serviceType, result) => {
        if (serviceType === 'json') {
            console.log(result);
        }
    });
    XtextServices.addSuccessListener((serviceType, result) => {
        if (serviceType === 'creatableObjectProposals') {
            console.log(result);
        }
    });

    XtextServices.getEmfModel();
    XtextServices.creatableEntityProposals();
};

export default testCode
