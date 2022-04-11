import React from 'react';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Grid from '@mui/material/Grid';
import SimpleInteractiveCharts from './SimpleInteractiveCharts';


import PropTypes from 'prop-types';
import './Table.css';

import barChartTableHeadIcon from '../images/dataset/bar_chart-table-head.svg';
import closeTableHeadIcon from '../images/dataset/close_24px.svg';


export default class DataTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showDataTable: true,
      selectedLabel: '',
      selectedColumn: -1,
    };
  }

  selectColumn(value, index) {
    this.setState({showDataTable: false, selectedLabel: value, selectColumn: index});
    console.log(value);
  }

  closeDataVisualisation(e) {
    e.stopPropagation();
    this.setState({showDataTable: true, selectedLabel: undefined, selectColumn: -1});
  }

  translate(value) {
    const translate = {
        averageNumberOfCapitalisedValues: 'capitalised values (ø)',
        averageNumberOfCharacters: 'characters (ø)',
        averageNumberOfDigits: 'digits (ø)',
        averageNumberOfSpecialCharacters: 'special characters (ø)',
        averageNumberOfTokens: 'tokens (ø)',
        numberOfDistinctValues: 'distinct values',
        numberOfInvalidValues: 'invalid values',
        numberOfNullValues: 'null values',
        numberOfValidNonNullValues: 'non null values',
        numberOfValidValues: 'valid values',
        numberOfValues:  'values',
        standardDeviation: 'standard deviation'    
    }
    return translate[value] ?? value;
  } 

  render () {
		return (
      <TableContainer style={{height: 'fit-content'}}>
        <Table aria-label="Data Table">
          <TableHead>
            <TableRow>
              {this.props.tableHeads.map((tableHead, index) => (
                <TableCell 
                  style={this.state.selectColumn === index ? {'backgroundColor': '#F2F2F2'} : {}}
                  align="left"
                  onClick={() => this.selectColumn(tableHead, index)}>
                    <div>{tableHead}</div>
                    <div style={{'fontSize': '0.8em' }}>{this.props.tableDataTypes[index]}</div>
                    {!this.state.showDataTable && this.state.selectColumn === index ?
                      <img 
                        src={closeTableHeadIcon} 
                        onClick={(e) => this.closeDataVisualisation(e)}
                        alt=""/>
                    :
                      <img src={barChartTableHeadIcon} alt=""/>
                    }
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          {this.state.showDataTable ?
            <TableBody>
              {this.props.tableBodies.map((tableBody) => (
                <TableRow key={tableBody[0]}>
                  {tableBody.map((element) => (
                      <TableCell align="left">{element}</TableCell>
                  ))}
                </TableRow>
              ))}
            </TableBody>
          :
            <TableBody>            
              <TableRow key={'test-1'} style={{'backgroundColor': '#F2F2F2'}}>
                    <TableCell align="left" colSpan={this.props.tableHeads.length}>
                      <Grid container spacing={{ xs: 2, md: 3 }} columns={{ xs: 4, sm: 8, md: 12 }}>
                        <Grid item xs={2} sm={2} md={2}>
                            {this.props.charts.filter(chart => chart.type === 'numeric' && chart.label === this.state.selectedLabel).map((chart) => (
                                <div><b>{this.translate(chart.key)}</b>: {chart.statistic.value}</div>
                            ))}
                        </Grid>
                        <Grid item xs={8} sm={6} md={10}>
                          <div className={'MuiTableCell-head simple-interactive-charts'}>
                            <SimpleInteractiveCharts
                              charts={this.props.charts.filter(chart => chart.type !== 'numeric' && chart.type !== 'list' && chart.label === this.state.selectedLabel)}
                              />
                          </div>
                        </Grid>

                      </Grid>

                    </TableCell>
              </TableRow>
            </TableBody>
          }
        </Table>

      </TableContainer>
    );
  }
}

DataTable.propTypes = {
  /**
   * List of string headers
   */
  tableHeads: PropTypes.array,
  /**
   * List of dataTypes
   */
  tableDataTypes: PropTypes.array,
   /**
	 * List of row arrays
   */
  tableBodies: PropTypes.array,
   /**
	 * List of charts arrays
   */
  charts: PropTypes.array

};