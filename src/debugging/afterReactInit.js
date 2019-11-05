import XtextServices from '../serverConnection/XtextServices';
import TextEditorWrapper from '../components/EditorView/TextEditor/TextEditorWrapper';
import { debugInterface } from './exposeToBrowserConsole';
import store from '../reduxStore';

let afterReactInit = () => {

    TextEditorWrapper.setText("// Load and prepare data\n" +
        'adacAugust = loadDataset("ADACAugust")\n' +
        "sampled = sample(adacAugust, 1000)\n" +
        "X = keepAttributes(sampled, [\n" +
        "   2  /* Floating Car Data point: has time (hour) */,\n" +
        "   3  /* Floating Car Data point: has time (day of week) */,\n" +
        "   4  /* Floating Car Data point: has time (month of year) */,\n" +
        "   6  /* Floating Car Data point: vehicle type (label) */,\n" +
        "   12 /* Street: type (label) */\n" +
        "])\n" +
        "y = keepAttributes(sampled, [\n" +
        "   7  /* Floating Car Data point: has speed */\n" +
        "])\n" +
        "// Train the model\n" +
        "model = LassoRegression() with {\n" +
        "   regularizationStrength : 1\n" +
        "}\n" +
        "trained_model = fit(model, X, y)\n" +
        "// Predict something and print the result\n" +
        "pred_X = [\n" +
        "   [23, 3, 8, 1, 2]\n" +
        "]\n" +
        "pred_y = predict(trained_model, pred_X)\n" +
        "write(pred_y)");

    XtextServices.addSuccessListener((serviceType, result) => {
        debugInterface.d.lsr = result;
        if(result.emfModel) {
            debugInterface.d.emf = {inSync: true, data: JSON.parse(result.emfModel)};
        } else {
            debugInterface.d.emf.inSync = false;
        }
    });

    XtextServices.addSuccessListener((serviceType, result) => {
        console.log({serviceType, result})
    });
}

export default afterReactInit;
