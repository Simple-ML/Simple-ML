import dslConfigurationDetails from "./dslConfigurationDetails.js"

class EmfModelHelper {

    /**
     * Flattens the hierarchical structure of the given EMF-Model (from backend).
     *
     * @param emfModelTree: {...} //from backend
     * @returns     [{
     *                  data: EmfModelData,
     *                  parent: ,
     *                  children: ,
     *                  self: name of property in parent
     *              },
     *              ...]
     */
    static flattenEmfModelTree(emfModelTree) {
        let flatEmfModel = [];

        this.recursion(flatEmfModel, emfModelTree, undefined, undefined);
        return flatEmfModel;
    }

    /**
     * Creates a string witch represents the complete hierarchy up to the root-node (separated by '/').
     * Example: Workflow(statement[3])/.../ProcessCall(arguments[0])
     *          Workflow: ClassName of parent
     *          statement[3]: PropertyName from parent and array-index if is one
     *
     * @param emfEntity
     * @param suffix
     * @returns string
     */
    static getFullHierarchy(emfEntity, suffix = '') {
        if(suffix === '' && emfEntity.data.className !== undefined)
            suffix = emfEntity.data.className.split('.').pop();
        if(emfEntity.parent !== undefined)
            return this.getFullHierarchy(emfEntity.parent, emfEntity.parent.data.className.split('.').pop() + '(' + emfEntity.self + ')/' + suffix);
        return suffix
    }

    /**
     * Like getFullHierarchy(...) but without parent-className.
     *
     * @param emfEntity
     * @param suffix
     * @returns string
     */
    static getFullHierarchy2(emfEntity, suffix = '') {
        if(emfEntity.parent !== undefined)
            return this.getFullHierarchy2(emfEntity.parent, emfEntity.self + '/' + suffix);
        return suffix;
    }

    /**
     * Creates a different representation of EmfEntity for easier processing.
     *
     * @param emfEntity
     * @returns {{data: Object, arrays: Array, objects: Array}}
     */
    static createMetaObject(emfEntity) {
        let data = {};
        let arrays = [];
        let objects = [];

        for(let propertyName in emfEntity) {
            let property = emfEntity[propertyName];

            if(Array.isArray(property)) {
                arrays.push({
                    name: propertyName,
                    data: property
                });
            } else if(typeof property === 'object') {
                objects.push({
                    name: propertyName,
                    data: property
                });
            } else {
                data[propertyName] = property;
            }
        }
        return { data, arrays, objects };
    }

    /**
     * Traverses the given EmfModel-Hierarchy and creates an Array of all elements
     *
     * @param emfEntityList
     * @param emfEntity
     * @param parent
     * @param self
     * @returns {{data: Object, parent: Object, children: Array, self: string}}
     */
    static recursion(emfEntityList, emfEntity, parent, self) {
        let { data, arrays, objects } = this.createMetaObject(emfEntity);
        let flatEntity = { data, parent, children: [], self, getValue: {}};

        arrays.forEach((array) => {
            array.data.forEach((element, index) => {
                flatEntity.children.push(this.recursion(emfEntityList, element, flatEntity, array.name + "[" + index + ']'));
            });
        });
        objects.forEach((object) => {
            flatEntity.children.push(this.recursion(emfEntityList, object.data, flatEntity, object.name));
        });
        emfEntityList.push(flatEntity);
        var dslConfigs = dslConfigurationDetails.filter(entity => entity.className === flatEntity.data.className)[0];
        if(dslConfigs !== undefined){
            flatEntity.getValue = dslConfigs.getValue;
        }
        return flatEntity;
    }
}

export default {
    flattenEmfModelTree: (emfModelTree) => EmfModelHelper.flattenEmfModelTree(emfModelTree),
    getFullHierarchy: (emfEntity) => EmfModelHelper.getFullHierarchy(emfEntity),
    getFullHierarchy2: (emfEntity) => EmfModelHelper.getFullHierarchy2(emfEntity)
}
