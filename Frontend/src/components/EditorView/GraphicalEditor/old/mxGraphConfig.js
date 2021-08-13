class MxGraphConfig {

    static dslPrefix = "de.unibonn.simpleml.simpleML.";
    static constants = {
            PROCESSCALL: 'ProcessCall',
            ASSIGNMENT: "Assignment",
            LOAD_DATASET: "loadDataset",
            SAMPLE: "sample",
            KEEP_ATTRIBUTES: "keepAttributes",
            TRAIN: "train",
            ML_MODEL: "mlModel"
        }
    static configs = this.defineConfig();


    static defineConfig() {
        /* defines the short name, class name, style, image and a template for new objects of the class. */
        var configs = [];
        configs.push(
            {
                className: this.dslPrefix + this.constants.ASSIGNMENT,
                style: this.constants.ASSIGNMENT,
                newNameTemplate: "newAssignment"
            }
        );
        configs.push(
            {
                className: this.dslPrefix + this.constants.PROCESSCALL,
                style: this.constants.PROCESSCALL,
                newNameTemplate: "newProcessCall"
            }
        );
        return configs;
    }

    static getName(className) {
        if (className) {
            return className.replace(this.dslPrefix, "")
        }
        return "";
    }

    /* adds the new class with that name and default values to the config */
    static addNewConfig(className) {
        var newClassConfigs =
        {
            className: className,
            style:"",
            newNameTemplate: "unknownObject"
        };
        this.configs.push(newClassConfigs);
        return newClassConfigs;
    }

    /* Matches the class to defined attributes.
    * if the input class name is unknown, calls addNewConfig and extends the existing config array with new configs
    * if the attribute type is unknown return null. */
    static getConfig(className, key) {
        var requiredClass = this.configs.filter(function (key) {
            return key.className === className;
        });
        if (requiredClass.length === 0) {
            var newClassConfigs = this.addNewConfig(className);
            requiredClass.push(newClassConfigs);
        }
        if (requiredClass[0][key]) {
            return requiredClass[0][key];
        }
        else {
            return null;
        }
    }

    static getLabelName(cell) {
        var name;
        switch (true) {
            case cell.value.data.ref !== undefined:
                name = "PROCESS:\n" + cell.value.data.ref;
                return name;
            case cell.value.data.value !== undefined:
                name = "CONFIG:\n" + cell.value.data.value;
                return name;
            case cell.value.data.name !== undefined:
                name = "DATASET:\n" + cell.value.data.name;
                return name;
            case cell.value.data.className === (this.dslPrefix + "DateLiteral"):
                name = "CONFIG:\n year: " + cell.value.data.year + ", month: " + cell.value.data.month + ", day: " + cell.value.data.day
                return name;
            case cell.value.self === "seconds":
                name = "CONFIG:\n hours: " + cell.value.parent.data.hours + ", minutes: " + cell.value.parent.data.minutes + ", seconds: " + cell.value.data.seconds;
                return name;
            case "":
            default:
                return '';
        }
    }

    static getStyle(entity) {
        var className = this.getName(entity.data.className);
        if (className !== this.constants.PROCESSCALL){

            return this.constants.ASSIGNMENT;   
        } else {
            switch (entity.data.ref){
                case "loadDataset":
                case "read_tsv":
                    return this.constants.LOAD_DATASET;
                case "sample":
                    return this.constants.SAMPLE;
                case "keepAttributes":
                    return this.constants.KEEP_ATTRIBUTES;
                case "LassoRegression":
                case "ElasticNetRegression":
                case "LinearRegression":
                case "DesicionTree":
                case "RidgeRegression":
                case "RandomForest":
                    return this.constants.ML_MODEL;
                case "fit":
                    return this.constants.TRAIN;
                case "predict":
                case "write":
                case "project":    
                default:
                    return this.constants.PROCESSCALL;
            }
        }
    }

    static isVisibleClass(className) {
        var name = this.getName(className);
        switch (name) {
            /*case "Workflow":
            case "Reference":
            case "UnconnectedExpressionStatement":
            case "ArrayLiteral":
            case "TimeLiteral":
            case "StringLiteral":
            case "DateLiteral":
                return false;
            default:
                return true;*/
            case "Assignment":
            case "ProcessCall":
                return true;
            default:
                return false;
        }
    }

    static isVisibleEntity(entity) {
        if (entity.self !== "ref" && this.isVisibleClass(entity.data.className)) {
            return true;
        } else {
            return false;
        }
    }
}

export default {
    dslPrefix: MxGraphConfig.dslPrefix,
    configs: MxGraphConfig.configs,
    constants: MxGraphConfig.constants,
    addNewConfig: (className) => MxGraphConfig.addNewConfig(className),
    getName: (className) => MxGraphConfig.getName(className),
    getStyle: (className) => MxGraphConfig.getStyle(className),
    getLabelName:(cell)=>MxGraphConfig.getLabelName(cell),
    isVisibleEntity: (entity) => MxGraphConfig.isVisibleEntity(entity)

}
