

class EmfModelHelper {

    /**
     * Flattens the hierarchical structure of the given EMF-Model (from backend).
     *
     * @param emfModelTree: comes from backend
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
     * Concatenates own name with parents name. Creates a string witch represents the complete hierarchy.
     *
     * @param emfEntity
     * @param suffix
     * @returns string
     */
    static getFullHierarchy(emfEntity, suffix = '') {
        if(emfEntity.parent !== undefined)
            return this.getFullHierarchy(emfEntity.parent, emfEntity.self + '/' + suffix);
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
        let flatEntity = { data, parent, children: [], self };

        arrays.forEach((array) => {
            array.data.forEach((element, index) => {
                flatEntity.children.push(this.recursion(emfEntityList, element, flatEntity, array.name + "[" + index + ']'));
            });
        });
        objects.forEach((object) => {
            flatEntity.children.push(this.recursion(emfEntityList, object.data, flatEntity, object.name));
        });
        emfEntityList.push(flatEntity);
        return flatEntity;
    }
}

export default {
    flattenEmfModelTree: (emfModelTree) => EmfModelHelper.flattenEmfModelTree(emfModelTree),
    getFullHierarchy: (emfEntity) => EmfModelHelper.getFullHierarchy(emfEntity)
}
