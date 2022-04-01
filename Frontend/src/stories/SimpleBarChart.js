import React, { useState, useEffect } from 'react';
import { ResponsiveBar } from '@nivo/bar';
import PropTypes from 'prop-types';
import './simpleBarChart.css'
export default class SimpleBarChart extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
		};
	}

	render() {
		return (
			<div>
				<div className="BarChartBackground">
					<div style={{ width: this.props.width, height: this.props.height }}>
						<ResponsiveBar
							data={this.props.barChart.bars}
							keys={['number_of_instances']}
							indexBy="value"
							margin={{ top: 50, right: 130, bottom: 50, left: 60 }}
							padding={0.3}
							colors={{ scheme: 'purple_orange' }}
							defs={[
								{
									id: 'dots',
									type: 'patternDots',
									background: 'inherit',
									color: '#38bcb2',
									size: 4,
									padding: 1,
									stagger: true
								},
								{
									id: 'lines',
									type: 'patternLines',
									background: 'inherit',
									color: '#eed312',
									rotation: -45,
									lineWidth: 6,
									spacing: 10
								}
							]}
							fill={[
								{
									match: {
										id: 'fries'
									},
									id: 'dots'
								},
								{
									match: {
										id: 'average_speed'
									},
									id: 'average_speed'
								}
							]}
							borderColor={{ from: 'color', modifiers: [['darker', 1.6]] }}
							axisTop={null}
							axisRight={null}
							axisBottom={{
								tickSize: 5,
								tickPadding: 5,
								tickRotation: 0,
								legendPosition: 'middle',
								legendOffset: 32
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

SimpleBarChart.defaultProps = {
	width: '60vw',
	height: '60vh'
};
SimpleBarChart.propTypes = {
	/**
	   * String for thWochentage title
	 */
	title: PropTypes.string,
	/**
	 * Bar Chart Object
	 */
	barChart: PropTypes.array,
	/**
	 * How high the chart is supposed to be can't be 0 else wont render (Logo string for the project name
	 */
	height: PropTypes.string,
	/**
	 * How wide the chart is supposed to be can't be 0 else wont render (Logo string for the project name
	 */
	width: PropTypes.string
};
