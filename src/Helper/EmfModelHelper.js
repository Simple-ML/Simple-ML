

export default class EmfModelHelper {

    flattenEmfModelTree = (emfModelTree) => {
        let flatEmfModel = [];

        this.recursion(flatEmfModel, emfModelTree, undefined, undefined);

        return flatEmfModel;
    }

    createMetaObject = (emfEntity) => {
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

    recursion = (emfEntityList, emfEntity, parent, self) => {
        let { data, arrays, objects } = this.createMetaObject(emfEntity);
        let flatEntity = { data, parent, children: [], self };

        arrays.forEach((array) => {
            array.data.forEach((element, index) => {
                flatEntity.children.push(this.recursion(emfEntityList, element, flatEntity, array.name + "|" + index));
            });
        });
        objects.forEach((object) => {
            flatEntity.children.push(this.recursion(emfEntityList, object.data, flatEntity, object.name));
        });

        emfEntityList.unshift(flatEntity);
        return flatEntity;
    }
}

