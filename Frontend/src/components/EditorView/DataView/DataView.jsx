import React from 'react';
import { connect } from 'react-redux';
import './dataView.scss'
import { experimentalStyled as styled } from '@mui/material/styles';
import Tab from '@mui/material/Tab';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Checkbox from '@mui/material/Checkbox';
import FormControlLabel from '@mui/material/FormControlLabel';
import TabContext from '@material-ui/lab/TabContext';
import TabPanel from '@material-ui/lab/TabPanel';
import TabList from '@material-ui/lab/TabList';
import SimpleBarChart from '../../../stories/SimpleBarChart.js';
import SimpleBoxPlotChart from '../../../stories/SimpleBoxPlotChart.js';
import SimpleHistogramChart from '../../../stories/SimpleHistogramChart.js';
import SimpleMapChart from '../../../stories/SimpleMapChart';

import Table from '../../../stories/Table.js';
import PropTypes from 'prop-types';
import { MultiSelect } from "react-multi-select-component";

class DataView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            value: "1",
            isLoaded: false,
            items: [],
            barCharts: [],
            histogramCharts: [],
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

    handleOnChange = (event) => {
        const selectedItems = [...this.state.selectedFilter];
        const filterOption = JSON.parse(event.target.value);
        const findIndex = selectedItems.findIndex((item) => item.label === filterOption.label && item.value === filterOption.value );
        if (findIndex === -1 && event.target.checked) {
            selectedItems.push(filterOption);            
        }
        if (findIndex !== -1 && !event.target.checked) {
            selectedItems.splice(findIndex, 1);            
        }
        this.setState({ selectedFilter: selectedItems.sort((a, b) => a.index > b.index ? 1 : a.index < b.index ? -1 : 0) })
        console.log(event.target.checked);
    };

    handleSelectAllOnChange = (event) => {
        if (event.target.checked) {
            this.setState({ selectedFilter: [...this.state.filterOptions] })
        } else {
            this.setState({ selectedFilter: [] })
        }
    };

    componentDidMount() {
        const selectedDataset = JSON.parse(this.props.dataset.dataset_json);
        var attributes = selectedDataset.attributes;
        var loadedCharts = [];
        var loadedFilterOptions = [];
        var index = 0;
        Object.keys(attributes).map(attributesKey => {
            loadedFilterOptions.push({ index: index++, label: attributes[attributesKey].label, value: attributesKey });
            Object.keys(attributes[attributesKey]["statistics"]).map(statisticsKey => {
                const loadedChart = {
                    key: statisticsKey,
                    type: attributes[attributesKey]["statistics"][statisticsKey]["type"],
                    label: attributes[attributesKey].label,
                    statistic: attributes[attributesKey]["statistics"][statisticsKey]
                };

                loadedCharts.push(loadedChart);
                return statisticsKey;
            })
            return attributesKey;
        })
        this.setState({
            isLoaded: true,
            items: selectedDataset,
            charts: loadedCharts,
            filterOptions: loadedFilterOptions,
            selectedFilter: []
        });
    }

    renderChart(chart, width='unset', height='300px') {
        switch (chart.type) {
            case 'histogram':
                return <SimpleHistogramChart
                    title={chart.label}
                    width={width}
                    height={height}
                    histogramChart={chart.statistic}
                />;

            case 'box_plot':
                return <div><SimpleBoxPlotChart
                    title={chart.label}
                    width={width}
                    height={height}
                    boxPlotChart={chart.statistic}
                /></div>

            case 'bar_chart':
                return <SimpleBarChart
                    title={chart.label}
                    width={width}
                    height={height}
                    barChart={chart.statistic}
                />;

                case 'spatial_value_distribution':
                    return <SimpleMapChart
                        title={chart.label}
                        width={width}
                        height={height}
                        mapChart={chart.statistic}
                    />;

            default:
                return <div></div>;
        }
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

    render() {

        const Item = styled(Paper)(({ theme }) => ({
            backgroundColor: theme.palette.mode === 'dark' ? '#1A2027' : '#fff',
            ...theme.typography.body2,
            padding: theme.spacing(2),
            textAlign: 'left',
            color: theme.palette.text.secondary,
          }));

        const GridLabel = (({label}) => (
            <div style={{ padding: '10px 0px 5px' }}>{label}</div>
        ));

        const { error, isLoaded, items, charts, selectedFilter, filterOptions } = this.state;
        if (error) {
            return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
            return <div>Loading...</div>;
        } else {

            return (
                <div className={'data-view'}>
                    <TabContext value={this.state.value}>
                        <div className={'dataset-title-group'}>
                            <div className={'dataset-title'}>{this.props.dataset.title}</div>
                            <div className={'dataset-count'}>{this.props.dataset.number_of_instances} rows, {items.sample_instances.header_labels.length} columns</div>
                        </div>
                        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                            <TabList onChange={this.handleChange}>
                                <Tab label="Data table" value="1" style={{ color: '#E84E1A', borderRadius: '4px', width: ' 140px', height: '30px', outline: '2px solid', marginTop: '5px', marginBottom: '5px', marginRight: '5px', marginLeft: '5px' }} />
                                <Tab label="Summary" value="2" style={{ color: '#E84E1A', borderRadius: '4px', width: ' 140px', height: '30px', outline: '2px solid', marginTop: '5px', marginBottom: '5px', marginRight: '50px', marginLeft: '5px' }} />
                            </TabList>
                        </Box>
                        <TabPanel value="1">
                            <div className={'table-view'}>
                                <Table style={{ outline: '2px solid' }}
                                    tableHeads={items.sample_instances.header_labels}
                                    tableDataTypes={items.sample_instances.data_types}
                                    tableBodies={items.sample_instances.lines}
                                    charts={charts}
                                />
                                <div className={'dataset-table-sub-group'}>
                                    <div className={'dataset-count'}>{items.sample_instances.lines.length} / {this.props.dataset.number_of_instances} rows, {items.sample_instances.header_labels.length} columns</div>
                                </div>
                            </div>
                        </TabPanel>
                        <TabPanel value="2">
                            <div className={'chart-view'}>

                                <Grid container spacing={{ xs: 2, md: 3 }} columns={{ xs: 4, sm: 8, md: 12 }}>
                                    <Grid item xs={2} sm={2} md={2}>
                                        <div className={'chart-filter'}>
                                            {/* <MultiSelect
                                                options={filterOptions}
                                                value={selectedFilter}
                                                onChange={this.handleSelection}
                                                labelledBy="Select"
                                            /> */}
                                            <div style={{'text-align': 'left'}}>
                                                <Box sx={{ display: 'flex', flexDirection: 'column', ml: 3 }}>
                                                <FormControlLabel
                                                        label={'select all'}
                                                        control={<Checkbox 
                                                            checked={this.state.selectedFilter?.length > 0 && this.state.selectedFilter?.length === this.state.filterOptions?.length}
                                                            indeterminate={this.state.selectedFilter?.length > 0 && this.state.selectedFilter?.length !== this.state.filterOptions?.length}
                                                            value={{'label': 'select all', 'value': false}} 
                                                            onChange={this.handleSelectAllOnChange}/>}
                                                    />

                                                    {filterOptions.map((filterOption) => (
                                                        <FormControlLabel
                                                            label={filterOption.label}
                                                            control={<Checkbox 
                                                                checked={this.state.selectedFilter?.findIndex((item) => item.label === filterOption.label) !== -1}
                                                                value={JSON.stringify(filterOption)} 
                                                                onChange={this.handleOnChange}/>}
                                                        />
                                                    ))}
                                                </Box>
                                            </div>
                                        </div>                                    
                                    </Grid>
                                    <Grid item xs={8} sm={6} md={10}>
                                        <div className={'chart-summary'}>
                                        {selectedFilter.map((selected) => (
                                            <div style={{ color: 'black', textAlign: 'left' }}>

                                                <Grid container spacing={{ xs: 2, md: 3 }} columns={{ xs: 4, sm: 8, md: 12 }}>
                                                    <Grid item xs={2} sm={2} md={2}>
                                                        <GridLabel label={selected.label}/>
                                                        <Item>
                                                        {charts.filter(chart => chart.type === 'numeric').map((chart) => (
                                                            chart.label === selected.label ? <div><b>{this.translate(chart.key)}</b>: {chart.statistic.value}</div> : <></>
                                                        ))}
                                                        </Item>
                                                    </Grid>

                                                    {charts.filter(chart => chart.type === 'list').map((chart) => (
                                                        chart.label === selected.label ? <Grid item xs={2} sm={2} md={2}><GridLabel label={selected.label}/><Item><div><b>{chart.key}</b>: {chart.statistic.values.map((value) => (<div>{value}</div> ))}</div></Item></Grid> : <></>
                                                    ))}

                                                    {charts.filter(chart => chart.type !== 'numeric' && chart.type !== 'list').map((chart) => (
                                                        chart.label === selected.label ? <Grid item xs={2} sm={2} md={2}><GridLabel label={selected.label}/><Item> {this.renderChart(chart)} </Item></Grid> : <></>
                                                    ))}

                                                    {/* <Grid item xs={2} sm={2} md={2}>
                                                        <GridLabel label={selected.label}/>
                                                        <Item>
                                                            <SimpleMapChart/>
                                                        </Item>
                                                    </Grid> */}

                                                </Grid>
                                                
                                            </div>
                                        ))}
                                        </div>
                                    </Grid>

                                </Grid>

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
