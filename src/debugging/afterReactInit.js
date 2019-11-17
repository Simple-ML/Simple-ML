import XtextServices from '../serverConnection/XtextServices';
import TextEditorWrapper from '../components/EditorView/TextEditor/TextEditorWrapper';
import { debugInterface } from './exposeToBrowserConsole';
import store from '../reduxStore';

let afterReactInit = () => {

    TextEditorWrapper.setText("// Load and prepare data\n" +
        "adacAugust = loadDataset(\"ADACAugust\")\n" +
        "sampled = sample(adacAugust, 1000)\n" +
        "features = keepAttributes(sampled, [\n" +
        "    \"timestamp-time-string-to-hour\"\n" +
        "])\n" +
        "target = keepAttributes(sampled, [\n" +
        "    \"velocity\"\n" +
        "])\n" +
        "\n" +
        "// Train the model\n" +
        "model = LassoRegression() with {\n" +
        "    regularizationStrength: 1\n" +
        "}\n" +
        "trained_model = fit(model, features, target)\n" +
        "\n" +
        "// Predict something and print the result\n" +
        "predictionFeatures = [\n" +
        "    [23]\n" +
        "]\n" +
        "prediction = predict(trained_model, predictionFeatures)\n" +
        "write(prediction)");

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
