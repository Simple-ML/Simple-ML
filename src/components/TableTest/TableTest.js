import React from 'react';
import connect from "react-redux/es/connect/connect";
import BootstrapTable from 'react-bootstrap-table-next';

import filterFactory, { textFilter } from 'react-bootstrap-table2-filter';

import DataPreparator from './DataPreparator';

function formatter(column, colIndex, { sortElement, filterElement }) {
    return (
        <div style={ { display: 'flex', flexDirection: 'column' } }>
            { filterElement }
            { column.text }
            { sortElement }
            <button
                onClick={() => console.log(column)}>
                click me
            </button>
        </div>
    );
}


const columns = DataPreparator.prepareColumns().map((item) => {
    return {headerFormatter: formatter, ...item}
});
const data = DataPreparator.prepareSampleData().map((item, i) => {
   return {id: i, ...item}
});

class TableTest extends React.Component {


    render() {
        return (
            <div className={'tableTest'} style={{background: 'white'}}>
                <BootstrapTable

                    keyField={'id'}
                    columns={columns}
                    data={data}
                    filter={ filterFactory() }
                    >

                </BootstrapTable>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
    }
};

const mapDispatchToProps = dispatch => {
    return {
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(TableTest);
