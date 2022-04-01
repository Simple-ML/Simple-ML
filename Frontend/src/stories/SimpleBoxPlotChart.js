import React, { Fragment } from 'react';
import { line } from "d3-shape";
import { ResponsiveMarimekko } from '@nivo/marimekko';
import moment from 'moment';
import PropTypes from 'prop-types';
import './simpleBoxPlotChart.css'

export default class SimpleBoxPlotChart extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
		};
	}

	dataTimePattern = 'DD.MM.YYYY, HH:mm:ss';

	valuesMinimumMillis = () => {
		const list_data_type = this.props.boxPlotChart.list_data_type;

		switch (list_data_type) {
			case 'datetime':
				return moment(this.props.boxPlotChart.values[0], this.dataTimePattern).toDate().getTime();

			default:
				return this.props.boxPlotChart.values[0];
		}

	}
	valuesMaximumMillis = () => {
		const list_data_type = this.props.boxPlotChart.list_data_type;
		switch (list_data_type) {
			case 'datetime':
				return moment(this.props.boxPlotChart.values[this.props.boxPlotChart.values.length - 1], this.dataTimePattern).toDate().getTime();

			default:
				return this.props.boxPlotChart.values[this.props.boxPlotChart.values.length - 1];
		}
	}

	data = () => {
		const list_data_type = this.props.boxPlotChart.list_data_type;
		const values = this.props.boxPlotChart.values;

		const min = this.valuesMinimumMillis();

		let returnBuckets = [];
		let lastValue = 0;
		switch (list_data_type) {
			case 'datetime':
				for (const value of values) {
					let newBucket = {};
					newBucket.bucketMinimum = lastValue;
					newBucket.bucketMaximum = (moment(value, this.dataTimePattern).toDate().getTime() - min);
					lastValue = newBucket.bucketMaximum;
					newBucket.value = 0.5;
					newBucket.part1 = 0.5;
					newBucket.part2 = 0.5;
					returnBuckets.push(newBucket);
				}
				console.log(JSON.stringify(returnBuckets));

				break;

			default:
				returnBuckets = [];
				for (const value of values) {
					let newBucket = {};
					newBucket.bucketMinimum = lastValue;
					newBucket.bucketMaximum = (value - min);
					lastValue = newBucket.bucketMaximum;
					newBucket.value = 0.5;
					newBucket.part1 = 0.5;
					newBucket.part2 = 0.5;
					returnBuckets.push(newBucket);
				}

				break;
		}
		for (const bucket of returnBuckets) {
			bucket.bucketValue = bucket.bucketMaximum - bucket.bucketMinimum;
		}

		if (returnBuckets.length === 2) {
			returnBuckets = [returnBuckets[0], ...returnBuckets];
		}

		if (returnBuckets.length === 3) {
			returnBuckets = [returnBuckets[0], ...returnBuckets, returnBuckets[returnBuckets.length-1]];
		}

		return returnBuckets;
	}

	tickValues = () => {
		const buckets = this.data();
		const tickValues = [buckets[0]?.bucketMinimum];
		for (const bucket of buckets) {
			tickValues.push(bucket.bucketMaximum);
		}

		return tickValues;
	}

	formatAxisBottom = (value) => {
		const list_data_type = this.props.boxPlotChart.list_data_type;
		const min = this.valuesMinimumMillis();
		switch (list_data_type) {
			case 'datetime':
				return moment(new Date().setTime(min + value)).format(this.dataTimePattern);

			default:
				break;
		}

		const valueNumber = min + value;

		return valueNumber.toLocaleString(navigator.language, { minimumFractionDigits: 0 });
	}
	render() {

		const Tooltip = (props) => {
			console.log('props: ' + props);

			const data = props.bar.datum.data.origin;

			return (
				<div
					style={{
						background: 'white',
						color: 'black',
						padding: '9px 12px',
						border: '1px solid #ccc',
					}}
				>
					<div>value: {data?.bucketMaximum}</div>
				</div>
			);

		};

		const VerticalLine = (bar) => {

			const points = [];

			points.push({x: bar.x + bar.width, y: bar.y + bar.height / 2 - bar.y / 2});
			points.push({x: bar.x + bar.width, y: bar.y + bar.height / 2 + bar.y / 2});

			const lineGenerator = line()
			.x(point => point.x)
			.y(point => point.y);

			return <path 
				d={lineGenerator(points)}
				fill="none"
				stroke={"black"}
				style={{ pointerEvents: "none" }}
			/>

		}

		const HorizontalLine = (bar) => {

			const points = [];

			points.push({x: bar.x, y: bar.y + bar.height / 2});
			points.push({x: bar.x + bar.width, y: bar.y + bar.height / 2});

			const lineGenerator = line()
			.x(point => point.x)
			.y(point => point.y);

			return <path 
				d={lineGenerator(points)}
				fill="none"
				stroke={"black"}
				style={{ pointerEvents: "none" }}
			/>

		}

		const Rect = (bars) => {

			const points = [];

			const startBar = bars[1];
			const endBar = bars[3];

			points.push({x: startBar.x + startBar.width, y: startBar.y + startBar.height / 2 - startBar.y / 2});
			points.push({x: startBar.x + startBar.width, y: startBar.y + startBar.height / 2 + startBar.y / 2});
			points.push({x: endBar.x + endBar.width, y: endBar.y + endBar.height / 2 + endBar.y / 2});
			points.push({x: endBar.x + endBar.width, y: endBar.y + endBar.height / 2 - endBar.y / 2});

			const lineGenerator = line()
			.x(point => point.x)
			.y(point => point.y);

			return <path 
				d={lineGenerator(points)}
				fill="#461f4c"
				stroke={"black"}
				style={{ pointerEvents: "none" }}
			/>

		}

		const BoxPlot = ({ bars }) => {

			const filteredBars = bars?.filter(bar => bar.id === 'part1');
			const circleBars = [filteredBars[0], filteredBars[1], filteredBars[3], filteredBars[4]];
			const horizontalBars = [filteredBars[1], filteredBars[4]];

			return (
				<Fragment>
					{Rect(filteredBars)}
					{filteredBars.map(bar => VerticalLine(bar))}
					{horizontalBars.map(bar => HorizontalLine(bar))}
					{circleBars.map(bar => (
						<circle
							key={bar.key}
							cx={bar.x + bar.width}
							cy={bar.y + bar.height / 2}
							r={4}
							fill="white"
							stroke={"black"}
							style={{ pointerEvents: "none" }}
						/>
					))}
				</Fragment>
			);
		};


		return (
			<div>
				<div className="BoxPlotChartBackground">
					<div style={{ width: this.props.width, height: this.props.height }}>
						<ResponsiveMarimekko
							data={this.data()}
							id="bucketValue"
							value="bucketValue"
							dimensions={[
								{
									id: "value",
									value: "value"
								},
								{
									id: "part1",
									value: "part1"
								},
								{
									id: "part2",
									value: "part2"
								},
							]}
							tooltip={Tooltip}
							innerPadding={0}
							margin={{ top: 50, right: 130, bottom: 100, left: 60 }}
							padding={0.3}
							colors={{ scheme: 'purple_orange' }}
							borderColor={{ from: 'color', modifiers: [['darker', 1.6]] }}
							axisTop={null}
							axisRight={null}
							xScale={{
								type: 'linear',
								min: this.tickValues()[0],
								max: this.tickValues()[this.tickValues().length - 1],

							}}
							axisBottom={{
								tickValues: this.tickValues(),
								tickRotation: -45,
								legendPosition: 'middle',
								legendOffset: 132,
								format: this.formatAxisBottom
							}}
							axisLeft={null}
							labelSkipWidth={12}
							labelSkipHeight={12}
							labelTextColor="white"
							animate={true}
							motionStiffness={90}
							motionDamping={15}
							layers={["grid", "axes", BoxPlot, "markers", "legends"]}
						/>
					</div>
				</div>
			</div>
		);
	}
}
//<div>{JSON.stringify(items)}</div>
SimpleBoxPlotChart.defaultProps = {
	width: '60vw',
	height: '30vh' 
};
SimpleBoxPlotChart.propTypes = {
	/**
	   * String for thWochentage title
	 */
	title: PropTypes.string,
	/**
	 * BoxPlot Chart Object
	 */
	boxPlotChart: PropTypes.object,
	/**
	 * How high the chart is supposed to be can't be 0 else wont render (Logo string for the project name
	 */
	height: PropTypes.string,
	/**
	 * How wide the chart is supposed to be can't be 0 else wont render (Logo string for the project name
	 */
	width: PropTypes.string,
};
