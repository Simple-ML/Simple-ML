
import data from './L3SDataExample';


export default class DataPreparator {
    static prepareColumns() {
        return data.result.attributes.map((item) => {
            return {
                dataField: '' + item.in_use_id,
                text: item.label,
                dataType: item.type,
                statistics: item.statistics
            }
        })
    }


    static prepareSampleData() {
        let labelMapping = data.result.sample_instances[0].map((headerLabel) => {
            return data.result.attributes.filter((featureMetaData) => {
                return featureMetaData.label === headerLabel
            }).map((featureMetaData) => {
                return featureMetaData.in_use_id
            });
        });
        return data.result.sample_instances.slice(1).map((sample) => {
            let result = {};
            sample.forEach((property, i) => {
                result['' + labelMapping[i]] = property;
            });
            return result
        })
    }
}
