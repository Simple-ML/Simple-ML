import React from 'react';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import PropTypes from 'prop-types';
export default class DataTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }

  render () {
		return (
      <TableContainer>
        <Table aria-label="customized table">
          <TableHead>
            <TableRow>
              {this.props.tableHeads.map((tableHead) => (
                <TableCell align="right">{tableHead}</TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {this.props.tableBodies.map((tableBody) => (
              <TableRow key={tableBody[0]}>
                {tableBody.map((element) => (
                    <TableCell align="right">{element}</TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
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
	 * List of row arrays
   */
  tableBodies: PropTypes.array
};