export default class MxGraphConfig {

    constructor() {
        this.configs = this.defineConfig();
        this.addNewConfig("new", this.configs);
    }

    defineConfig() {
        /* defines the short name, class name, style, image and a template for new objects of the class. */
        var configs = [];
        configs.push(
            {
                name: "Assignment",
                className: "org.xtext.example.mydsl.myDsl.Assignment",
                style: "shape=cylinder;fillColor=#B891BF",
                image: "editors/images/rectangle.gif",
                newNameTemplate: "newAssignment"
            }
        );
        configs.push(
            {
                name: "PROCESS",
                className: "org.xtext.example.mydsl.myDsl.ProcessCall",
                style: "shape=rectangle;fillColor=#B891BF",
                image: "editors/images/rectangle.gif",
                newNameTemplate: "newAssignment"
            }
        );
        return configs;
    }

    /* adds the new class with that name and default values to the config */
    addNewConfig(className, configs) {
        var newClassConfigs =
        {
            name: "unknown",
            className: className,
            style: "shape=hexagon",
            image: "editors/images/symbols/error.png",
            newNameTemplate: "unknownObject"
        };
        configs.push(newClassConfigs);
        return newClassConfigs;
    }

    /* Matches the class to defined attributes.
    * if the input class name is unknown, calls addNewConfig and extends the existing config array with new configs
    * if the attribute type is unknown return null. */
    getConfig(className, key, configs) {
        var requiredClass = configs.filter(function (key) {
            return key.className === className;
        });
        if (requiredClass.length === 0) {
            var newClassConfigs = this.addNewConfig(className, configs);
            requiredClass.push(newClassConfigs);
        }
        if (requiredClass[0][key]) {
            return requiredClass[0][key];
        }
        else {
            return null;
        }
    }
}
