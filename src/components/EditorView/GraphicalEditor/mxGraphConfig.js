import datasetIcon from './../../../images/graph/instances/dataset.svg'
import defaultActionIcon from './../../../images/graph/instances/default-action.svg'
import plusIcon from './../../../images/graph/instances/plus.svg'
export default class MxGraphConfig {

    constructor() {
        this.dslPrefix = "org.xtext.example.mydsl.myDsl.";
        this.configs = this.defineConfig();
    }

    defineConfig() {
        /* defines the short name, class name, style, image and a template for new objects of the class. */
        var configs = [];
        configs.push(
            {
                name: "Assignment",
                className: this.dslPrefix + "Assignment",
                style: "shape=cylinder;fillColor=#B891BF",
                image: datasetIcon,
                newNameTemplate: "newAssignment"
            }
        );
        configs.push(
            {
                name: "ProcessCall",
                className: this.dslPrefix + "ProcessCall",
                style: "shape=rectangle;fillColor=#B891BF",
                image: defaultActionIcon,
                newNameTemplate: "newAssignment"
            }
        );
        configs.push(
            {
                name: "PlusVertex",
                className:"PlusVertex",
                style: "shape=image",
                image: plusIcon,
                newNameTemplate: "newAssignment"
            }
        );
        return configs;
    }

    getName(className) {
        if (className) {
            return className.replace(this.dslPrefix, "")
        }
        return "";
    }

    /* adds the new class with that name and default values to the config */
    addNewConfig(className) {
        var name;
        if (className !== undefined) {
            name = this.getName(className);
        } else {
            name = "unknown";
        }
        var newClassConfigs =
        {
            name: name,
            className: className,
            style: "shape=hexagon",
            image: "editors/images/symbols/error.png",
            newNameTemplate: "unknownObject"
        };
        this.configs.push(newClassConfigs);
        return newClassConfigs;
    }

    /* Matches the class to defined attributes.
    * if the input class name is unknown, calls addNewConfig and extends the existing config array with new configs
    * if the attribute type is unknown return null. */
    getConfig(className, key) {
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

    getLabelName(cell) {
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

    getStyle(className) {
        return this.getConfig(className, "style");
    }

    isVisibleClass(className) {
        var name = this.getName(className);
        switch (name) {
            case "Workflow":
            case "Reference":
            case "UnconnectedExpressionStatement":
            case "ArrayLiteral":
            case "TimeLiteral":
                return true;
            default:
                return false;
        }
    }

    isVisibleEntity(entity) {
        if (entity.self !== "ref" && !this.isVisibleClass(entity.data.className)) {
            return true;
        } else {
            return false;
        }
    }
}
