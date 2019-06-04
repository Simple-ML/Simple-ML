import XtextServices from "./ServerConnection/xtextServices";


let testCode = () => {
    XtextServices.addSuccessListener((serviceType, result) => {
            console.log({serviceType, result});
    });

    XtextServices.getEmfModel();
    XtextServices.creatableEntityProposals();
};

export default testCode
