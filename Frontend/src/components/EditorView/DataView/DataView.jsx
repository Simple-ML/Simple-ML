import React from 'react';
import { connect } from 'react-redux';
import './dataView.scss'
import Tab from '@mui/material/Tab';
import Box from '@mui/material/Box';
import TabContext from '@material-ui/lab/TabContext';
import TabPanel from '@material-ui/lab/TabPanel';
import TabList from '@material-ui/lab/TabList';
import  SimpleBarChart from '../../../stories/SimpleBarChart.js';
import  SimpleHistogramChart from '../../../stories/SimpleHistogramChart.js';
import  Table from '../../../stories/Table.js';
import PropTypes from 'prop-types';

class DataView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            value: "1",
            isLoaded: false,
            items:[],
            barCharts:[],
            histogramCharts:[],
            error: null
        };
    }

    handleChange = (event, newValue) => {
        this.setState({ value: newValue })
    };

    componentDidMount() {
        fetch(this.props.url)
        .then(res => res.json())
        .then(
            (result) => {
                var attributes = result.attributes;
                var statistics = Object.keys(attributes).map(key => {
                    return (attributes[key]["statistics"]);
                })
                var loadedBarCharts = [];
                var loadedHistogramCharts = [];
                statistics.forEach(statistic => 
                    Object.keys(statistic).map(key => {
                        if(key === 'value_distribution' && statistic[key]["type"] === 'bar_chart') {
                            loadedBarCharts.push(statistic[key]["bars"]);
                        }
                        if(key === 'histogram' && statistic[key]["type"] === 'histogram') {
                            loadedHistogramCharts.push(statistic[key]["buckets"]);
                        }
                    })
                );
                this.setState({
                isLoaded: true,
                items: result,
                barCharts: loadedBarCharts,
                histogramCharts: loadedHistogramCharts
                });
            },
            (error) => {
                this.setState({
                isLoaded: true,
                error
                });
            }
        )
    }

    render() {
        const { error, isLoaded, items, barCharts, histogramCharts } = this.state;
        if (error) {
          return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
          return <div>Loading...</div>;
        } else {
            return(
                <div className={'data-view'}>
                    <TabContext value={this.state.value}>
                        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                        <TabList onChange={this.handleChange} aria-label="lab API tabs example">
                            <Tab label="Data table" value="1" />
                            <Tab label="Summary" value="2" />
                        </TabList>
                        </Box>
                        <TabPanel value="1">
                            <Table
                                tableHeads={items.sample_instances.header_labels}
                                tableBodies={items.sample_instances.lines}
                            />
                        </TabPanel>
                        <TabPanel value="2">
                            {barCharts.map((barChart) => (
                                <SimpleBarChart barChart={barChart}/>
                            ))}
                            {histogramCharts.map((histogramChart) => (
                                <SimpleHistogramChart histogramChart={histogramChart}/>
                            ))}
                        </TabPanel>
                    </TabContext>
                </div>
            )
        }
    }
}

DataView.propTypes = {
    /**
     * Url where the data is being pulled from
     */
    url: PropTypes.string
};

const mapStateToProps = state => {
    return {
    }
};

const mapDispatchToProps = dispatch => {
    return {
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(DataView);
