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
        debugInterface.d.lsr = result;
        if(result.emfModel) {
            debugInterface.d.emf = {inSync: true, data: JSON.parse(result.emfModel)};
        } else {
            debugInterface.d.emf.inSync = false;
        }
    });

    XtextServices.addSuccessListener((serviceType, result) => {
        //console.log({serviceType, result})
    });
}

export default afterReactInit;
