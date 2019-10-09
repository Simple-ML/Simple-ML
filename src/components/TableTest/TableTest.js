import React from 'react';
import connect from "react-redux/es/connect/connect";
import BootstrapTable from 'react-bootstrap-table-next';
//import test from './bootstrap.module.scss';

import filterFactory, { textFilter } from 'react-bootstrap-table2-filter';


import data from './L3SapiData';

function priceFormatter(column, colIndex, { sortElement, filterElement }) {
    return (
        <div style={ { display: 'flex', flexDirection: 'column' } }>
            { filterElement }
            { column.text }
            { sortElement }
        </div>
    );
}

const columns = [
    {
        dataField: 'id',
        text: 'id',
        sort: true,
    },
    {
        dataField: 'vehicle type',
        text: 'vehicle type',
        sort: true,
    },
    {
        dataField: 'speed',
        text: 'speed',
        sort: true,
        filter: textFilter()
    },
    {
        dataField: 'Street',
        text: 'Street',
        sort: true,
        filter: textFilter()
    },
    {
        dataField: 'maximum speed',
        text: 'maximum speed',
        sort: true,
        filter: textFilter()
    }];

const data2 = data.map((item, i) => {
   return {id: i, ...item}
});

class TableTest extends React.Component {


    render() {
        return (
            <div className={'tableTest'} style={{background: 'white'}}>
                <BootstrapTable

                    keyField={'id'}
                    columns={columns}
                    data={data2}
                    filter={ filterFactory() }>

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
