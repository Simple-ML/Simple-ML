import EmfModelHelper from "../helper/EmfModelHelper";
import XtextServices from "../serverConnection/XtextServices";

let afterReactInit = () => {

    XtextServices.addSuccessListener((serviceType, result) => {
        console.log({serviceType, result});
    });

    XtextServices.addSuccessListener((serviceType, result) => {
        if (serviceType === 'getEmfModel') {
            console.log(EmfModelHelper.flattenEmfModelTree(JSON.parse(result.emfModel)));
        }
    });

    XtextServices.addSuccessListener((serviceType, result) => {
        if (serviceType === 'getEmfModel') {
            EmfModelHelper.flattenEmfModelTree(JSON.parse(result.emfModel)).forEach((element) => {
                console.log(EmfModelHelper.getFullHierarchy(element));
            });
        }
    });
}

export default afterReactInit;
