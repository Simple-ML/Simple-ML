import React from 'react';
import { connect } from 'react-redux';
import './dataView.scss'
import Tab from '@mui/material/Tab';
import Box from '@mui/material/Box';
import TabContext from '@material-ui/lab/TabContext';
import TabPanel from '@material-ui/lab/TabPanel';
import TabList from '@material-ui/lab/TabList';
import SimpleBarChart from '../../../stories/SimpleBarChart.js';
import SimpleHistogramChart from '../../../stories/SimpleHistogramChart.js';
import Table from '../../../stories/Table.js';
import PropTypes from 'prop-types';
import { MultiSelect } from "react-multi-select-component";

class DataView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            value: "1",
            isLoaded: false,
            items:[],
            barCharts:[],
            histogramCharts:[],
            error: null,
            filterOptions: [],
            selectedFilter: []
        };
    }

    handleChange = (event, newValue) => {
        this.setState({ value: newValue })
    };

    handleSelection = (selectedItems) => {
        this.setState({ selectedFilter: selectedItems })
    };

    componentDidMount() {
        const selectedDataset = JSON.parse(this.props.dataset.dataset_json);
        var attributes = selectedDataset.attributes;
        var loadedBarCharts = [];
        var loadedHistogramCharts = [];
        var loadedFilterOptions = [];
        Object.keys(attributes).map(attributesKey => {
            loadedFilterOptions.push({label: attributes[attributesKey].label, value: attributesKey});
            Object.keys(attributes[attributesKey]["statistics"]).map(statisticsKey => {
                if(statisticsKey === 'value_distribution' && attributes[attributesKey]["statistics"][statisticsKey]["type"] === 'bar_chart') {
                    loadedBarCharts.push({label: attributes[attributesKey].label , statistic: attributes[attributesKey]["statistics"][statisticsKey]["bars"]});
                }
                if(statisticsKey === 'histogram' && attributes[attributesKey]["statistics"][statisticsKey]["type"] === 'histogram') {
                    loadedHistogramCharts.push({label: attributes[attributesKey].label , statistic: attributes[attributesKey]["statistics"][statisticsKey]["buckets"]});
                }
            })
        })
        this.setState({
            isLoaded: true,
            items: selectedDataset,
            barCharts: loadedBarCharts,
            histogramCharts: loadedHistogramCharts,
            filterOptions: loadedFilterOptions,
            selectedFilter: loadedFilterOptions
        });
    }
    

    render() {
        const { error, isLoaded, items, barCharts, histogramCharts, selectedFilter, filterOptions } = this.state;
        if (error) {
          return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
          return <div>Loading...</div>;
        } else { 
            return(
                <div className={'data-view'}>
                    <TabContext value={this.state.value}>
                        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                        <TabList onChange={this.handleChange}>
                            <Tab label="Data table" value="1" style= {{color: '#E84E1A', borderRadius: '4px', width:' 140px', height: '36px', outline: '2px solid', marginTop: '5px', marginBottom: '5px', marginRight: '5px', marginLeft: '5px'}}/>
                            <Tab label="Summary" value="2" style= {{color: '#E84E1A', borderRadius: '4px', width:' 140px', height: '36px', outline: '2px solid', marginTop: '5px', marginBottom: '5px', marginRight: '50px', marginLeft: '5px'}}/>
                        </TabList>
                        </Box>
                        <TabPanel value="1">
                            <div className={'table-view'}>
                                <Table style= {{outline: '2px solid'}}
                                    tableHeads={items.sample_instances.header_labels}
                                    tableBodies={items.sample_instances.lines}
                                />
                            </div>
                        </TabPanel>
                        <TabPanel value="2">
                            <div className={'chart-view'}>
                            <div className={'chart-filter'}>
                                <h1>Filter Data</h1>
                                <MultiSelect
                                    options={filterOptions}
                                    value={selectedFilter}
                                    onChange={this.handleSelection}
                                    labelledBy="Select"
                                />
                            </div>
                                {barCharts.map((barChart) => (
                                    selectedFilter.map((selected) => (
                                        barChart.label === selected.label ? (
                                                <SimpleBarChart
                                                    title={barChart.label}
                                                    width='unset'
                                                    //height='20vh'
                                                    barChart={barChart.statistic}
                                                />
                                        ): <div></div>
                                    ))
                                ))}
                                {histogramCharts.map((histogramChart) => (
                                    selectedFilter.map((selected) => (
                                        histogramChart.label === selected.label ? (
                                                <SimpleHistogramChart 
                                                    title={histogramChart.label}
                                                    width='unset'
                                                    //height='20vh'
                                                    histogramChart={histogramChart.statistic}
                                                />
                                        ): <div></div>
                                    ))
                                ))}
                            </div>
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
