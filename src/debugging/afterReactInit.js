import EmfModelHelper from "../helper/EmfModelHelper";
import XtextServices from "../serverConnection/XtextServices";
import TextEditorWrapper from "../components/EditorView/TextEditor/TextEditorWrapper";
import { debugInterface } from "./exposeToBrowserConsole"


let afterReactInit = () => {

    TextEditorWrapper.setText('\n' +
        'dataframe1 = read_tsv("data/data.tsv")\n' +
        'x = project(dataframe1, ["dow"])\n' +
        'y = project(dataframe1, DATE 02-03-04, x)\n' +
        '\n' +
        '$UNCONNECTED NODES (only relevant for graphical DSL)$\n' +
        '\n' +
        'TIME 01:03:05\n' +
        'read_tsv("something")');


    XtextServices.addSuccessListener((serviceType, result) => {
        debugInterface.d.lsr = result
    });

    XtextServices.addSuccessListener((serviceType, result) => {
        if (serviceType === 'getEmfModel') {
        }
    });

    XtextServices.addSuccessListener((serviceType, result) => {
        if (serviceType === 'getEmfModel') {
            EmfModelHelper.flattenEmfModelTree(JSON.parse(result.emfModel)).forEach((element) => {
            });
        }
    });
}

export default afterReactInit;
