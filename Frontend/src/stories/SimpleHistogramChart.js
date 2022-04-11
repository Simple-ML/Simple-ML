import React from 'react';
import { ResponsiveMarimekko } from '@nivo/marimekko';
import moment from 'moment';
import PropTypes from 'prop-types';
import './simpleHistogramChart.css'

export default class SimpleHistogramChart extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
		};
	}

	dataTimePattern = 'DD.MM.YYYY, HH:mm:ss';

	bucketMinimumMillis = () => {
		const bucket_data_type = this.props.histogramChart.bucket_data_type;

		switch (bucket_data_type) {
			case 'datetime':
				return moment(this.props.histogramChart.buckets[0]?.bucketMinimum, this.dataTimePattern).toDate().getTime();

			default:
				return this.props.histogramChart.buckets[0]?.bucketMinimum;
		}

	}
	bucketMaximumMillis = () => {
		const bucket_data_type = this.props.histogramChart.bucket_data_type;
		switch (bucket_data_type) {
			case 'datetime':
				return moment(this.props.histogramChart.buckets[this.props.histogramChart.buckets.length - 1]?.bucketMaximum, this.dataTimePattern).toDate().getTime();

			default:
				return this.props.histogramChart.buckets[this.props.histogramChart.buckets.length - 1]?.bucketMaximum;
		}
	}

	data = () => {
		const bucket_data_type = this.props.histogramChart.bucket_data_type;
		const buckets = this.props.histogramChart.buckets;

		const min = this.bucketMinimumMillis();
		const max = this.bucketMaximumMillis();

		let returnBuckets = [];
		switch (bucket_data_type) {
			case 'datetime':
				for (const bucket of buckets) {
					const newBucket = {};

					console.log(bucket.bucketMaximum + ' ->');
					newBucket.origin = bucket;
					newBucket.bucketMinimum = (moment(bucket.bucketMinimum, this.dataTimePattern).toDate().getTime() - min);
					newBucket.bucketMaximum = (moment(bucket.bucketMaximum, this.dataTimePattern).toDate().getTime() - min);
					newBucket.value = bucket.value;
					console.log(max);
					returnBuckets.push(newBucket);
				}
				console.log(JSON.stringify(returnBuckets));

				break;

			default:
				returnBuckets = [];
				for (const bucket of buckets) {
					const newBucket = {};

					newBucket.origin = bucket;
					newBucket.bucketMinimum = (bucket.bucketMinimum - min);
					newBucket.bucketMaximum = (bucket.bucketMaximum - min);
					newBucket.value = bucket.value;
					returnBuckets.push(newBucket);
				}

				break;
		}
		for (const bucket of returnBuckets) {
			bucket.bucketValue = bucket.bucketMaximum - bucket.bucketMinimum;
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
		const bucket_data_type = this.props.histogramChart.bucket_data_type;
		const min = this.bucketMinimumMillis();
		switch (bucket_data_type) {
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
					<div>bucketMinimum: {data?.bucketMinimum}</div>
					<div>bucketMaximum: {data?.bucketMaximum}</div>
					<div>value: {data?.value}</div>
				</div>
			);

		};

		return (
			<div>
				<div className="HistogramChartBackground">
					<div style={{ width: this.props.width, height: this.props.height }}>
						<ResponsiveMarimekko
							data={this.data()}
							id="bucketValue"
							value="bucketValue"
							dimensions={[
								{
									id: "value",
									value: "value"
								}
							]}
							tooltip={Tooltip} 
							innerPadding={0}
							margin={{ top: 10, right: 10, bottom: 100, left: 70 }}
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
							axisLeft={{
								tickSize: 5,
								tickPadding: 5,
								tickRotation: 0,
								legend: 'count',
								legendPosition: 'middle',
								legendOffset: -40
							}}
							labelSkipWidth={12}
							labelSkipHeight={12}
							labelTextColor="white"
							animate={true}
							motionStiffness={90}
							motionDamping={15}
						/>
					</div>
				</div>
			</div>
		);
	}
}
//<div>{JSON.stringify(items)}</div>
SimpleHistogramChart.defaultProps = {
	width: '60vw',
	height: '60vh'
};
SimpleHistogramChart.propTypes = {
	/**
	   * String for thWochentage title
	 */
	title: PropTypes.string,
	/**
	 * Histogram Chart Object
	 */
	histogramChart: PropTypes.object,
	/**
	 * How high the chart is supposed to be can't be 0 else wont render (Logo string for the project name
	 */
	height: PropTypes.string,
	/**
	 * How wide the chart is supposed to be can't be 0 else wont render (Logo string for the project name
	 */
	width: PropTypes.string,
};
