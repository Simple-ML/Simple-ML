import XtextServices from '../serverConnection/XtextServices';
import EmfModelHelper from '../helper/EmfModelHelper';
import TextEditorWrapper from '../components/EditorView/TextEditor/TextEditorWrapper';
import { debugInterface } from './exposeToBrowserConsole';
import store from '../reduxStore';

let afterReactInit = () => {
    TextEditorWrapper.setText("// Load and prepare data\n" +
        "import simpleml.collections.*\n" +
        "import simpleml.collection.*\n" +
        "import simpleml.dataset.*\n" +
        "import simpleml.model.regression.*" +
        "\n" +
        "workflow predictSpeed {\n" +
        "\n" +
        "    // Load and prepare data\n" +
        '    val adacAugustSample = loadDataset("ADACAugust").sample(n_instances = 1000);\n' +
        "    val features = adacAugustSample.keepAttributes(\n" +
        "        2  /* Floating Car Data point: has time (hour) */,\n" +
        "        3  /* Floating Car Data point: has time (day of week) */, \n" +
        "        4  /* Floating Car Data point: has time (month of year) */,\n" +
        "        6  /* Floating Car Data point: vehicle type (label) */,\n" +
        "        12 /* Street: type (label) */\n" +
        "    );\n" +
        "    val target = adacAugustSample.keepAttributes(\n" +
        "        7  /* Floating Car Data point: has speed */\n" +
        "    );\n" +
        "\n" +
        "    // Define the model\n" +
        "    val model = Lasso(regularizationStrength = 0);\n" +
        "    \n" +
        "    // Train the model\n" +
        "    val trained_model = model.fit(features, target);\n" +
        "\n" +
        "    // Predict something and print the result\n" +
        "    val predictionFeatures = listOf(\n" +
        "        listOf(23, 3, 8, 1, 2)\n" +
        "    );\n" +
        "    val predictedTargets = trained_model.predict(features = predictionFeatures);\n" +
        "}"
    );

        XtextServices.addSuccessListener((serviceType, result) => {
            debugInterface.d.lsr = result;
            if(result.emfModel) {
                let emfModel = JSON.parse(result.emfModel);
                debugInterface.d.emf = {inSync: true, data: emfModel};
                debugInterface.d.emf_flat = EmfModelHelper.flattenEmfModelTree(emfModel);
                debugInterface.d.emf_renderable = EmfModelHelper.getRenderableEmfEntities(debugInterface.d.emf_flat);
                debugInterface.d.emf_associations = EmfModelHelper.getEmfEntityAssociations(debugInterface.d.emf_flat);
            } else {
                debugInterface.d.emf.inSync = false;
            }
        });
    

    XtextServices.addSuccessListener((serviceType, result) => {
        console.log({serviceType, result})
    });
}

export default afterReactInit;
