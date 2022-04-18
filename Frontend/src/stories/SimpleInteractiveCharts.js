import React from 'react';

import {Tab, Tabs} from '@mui/material';
import {TabPanel, TabContext} from '@material-ui/lab';

import SimpleBarChart from './SimpleBarChart.js';
import SimpleBoxPlotChart from './SimpleBoxPlotChart.js';
import SimpleHistogramChart from './SimpleHistogramChart.js';
import SimpleMapChart from './SimpleMapChart';

import PropTypes from 'prop-types';
import './simpleInteractiveCharts.css';

import MapIcon from '../images/dataset/Map.svg';
import SelectedMapIcon from '../images/dataset/Map_selected.svg';
import PlotIcon from '../images/dataset/Plot.svg';
import SelectedPlotIcon from '../images/dataset/Plot_selected.svg';
import BarChartIcon from '../images/dataset/Bar_Chart.svg';
import SelectedBarChartIcon from '../images/dataset/Bar_Chart_selected.svg';
import MultiLineChartIcon from '../images/dataset/Multiline_Chart.svg';

export default class SimpleInteractiveCharts extends React.Component {

	constructor(props) {
		super(props);

        let count = 0;
        this.props.charts.map((chart) => {
            chart.index = '' + count;
            chart.value = chart.label + '_' + chart.index;
            count++;
            return chart;
        });

        this.state = {
            value: this.props.charts[0]?.value,
            refresh: false,
            charts: this.props.charts
        };

	}

    componentDidMount() {
        // this.setState({ value: this.props.charts[0]?.value, charts: this.props.charts });
    }

    componentWillReceiveProps(props) {
        const { charts, refresh } = props;

        let count = 0;
        charts.map((chart) => {
            chart.index = '' + count;
            chart.value = chart.label + '_' + chart.index;
            count++;
            return chart;
        });

        const chart = charts[0];
        this.setState({ value: chart?.label + '_' + chart?.index, refresh: refresh, charts: charts });

        this.forceUpdate();
    }
    
    renderChart(chart, width='unset', height='800px') {
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
                    return <div><SimpleMapChart
                        title={chart.label}
                        width={width}
                        height={height}
                        mapChart={chart.statistic}
                    /></div>;

            default:
                return <div></div>;
        }
    }

    renderIcon(chart) {
        switch (chart.type) {
            case 'histogram':
                return <img className={'tab-img'} src={chart.value === this.state.value ? SelectedBarChartIcon : BarChartIcon} alt=""/>;

            case 'box_plot':
                return <img className={'tab-img plot-icon'} src={chart.value === this.state.value ? SelectedPlotIcon : PlotIcon} alt=""/>;

            case 'bar_chart':
                return <img className={'tab-img'} src={chart.value === this.state.value ? SelectedBarChartIcon : BarChartIcon} alt=""/>;

            case 'spatial_value_distribution':
                return <img className={'tab-img'} src={chart.value === this.state.value ? SelectedMapIcon : MapIcon} alt=""/>;

            default:
                return <div></div>;
        }
    }

    handleChange = (event, newValue) => {
        console.log(newValue);
        this.setState({ value: newValue })
    };

	render() {

		return (
			<div>
                <div className="InteractiveChartsBackground" style={{ width: this.props.width, height: this.props.height }}>
                    <TabContext value={this.state.value}>
                        <div className={'tab-container'}>
                            <Tabs
                                orientation="vertical"
                                aria-label="select chart"
                                onChange={this.handleChange}
                                sx={{ borderRight: 1, borderColor: 'divider' }}
                            >
                                {this.state.charts.map((chart) => (
                                    <Tab value={chart.value} icon={this.renderIcon(chart)} />
                                ))}
                                
                            </Tabs>

                        </div>

                        {this.state.charts.map((chart) => (
                            <TabPanel value={chart.value} index={chart.index}>
                                <div className="inner-tabpanel">
                                {this.renderChart(chart)}
                                </div>
                            </TabPanel>
                        ))}
                    </TabContext>
                </div>
			</div>
		);
	}
}
//<div>{JSON.stringify(items)}</div>
SimpleInteractiveCharts.defaultProps = {
	width: 'unset',
	height: '860px' 
};
SimpleInteractiveCharts.propTypes = {
	/**
	   * String for thWochentage title
	 */
	title: PropTypes.string,
	/**
	 * Chart Objects
	 */
	charts: PropTypes.array,
	/**
	 * How high the chart is supposed to be can't be 0 else wont render (Logo string for the project name
	 */
	height: PropTypes.string,
	/**
	 * How wide the chart is supposed to be can't be 0 else wont render (Logo string for the project name
	 */
	width: PropTypes.string,

    refresh: PropTypes.bool
}; 
