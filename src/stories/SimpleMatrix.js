import React from 'react';
import {ResponsiveHeatMap} from '@nivo/heatmap';
import PropTypes from 'prop-types';
export default class SimpleMatrix extends React.Component {
  constructor(props) {
    super(props);
  }
  render() {
    const data = this.props.data;
		return (
      <div style={{width:this.props.width,height:this.props.height}}>
				<h1> {this.props.title}</h1>
        <ResponsiveHeatMap
          data={data}
          keys={[
            'Street Type',
            'Max Speed',
            'Location',
            'Average Speed',
            'Daylight',
            'Day Type',
          ]}
          colors="PRGn"
          colors="purples"
          indexBy="country"
          margin={{top: 100, right: 60, bottom: 60, left: 60}}
          forceSquare={true}
          axisTop={{
            orient: 'top',
            tickSize: 5,
            tickPadding: 5,
            tickRotation: -90,
            legend: '',
            legendOffset: 36,
          }}
          axisRight={null}
          axisBottom={null}
          axisLeft={{
            orient: 'left',
            tickSize: 5,
            tickPadding: 5,
            tickRotation: 0,
            legendPosition: 'middle',
            legendOffset: -40,
          }}
          cellOpacity={1}
          cellBorderColor={{from: 'color', modifiers: [['darker', 0.4]]}}
          labelTextColor="#292929"
          defs={[
            {
              id: 'lines',
              type: 'patternLines',
              background: 'inherit',
              color: 'rgba(0, 0, 0, 0.1)',
              rotation: -45,
              lineWidth: 4,
              spacing: 7,
            },
          ]}
          fill={[{id: 'lines'}]}
          animate={true}
          motionStiffness={80}
          motionDamping={9}
          hoverTarget="cell"
          cellHoverOthersOpacity={0.25}
        />
      </div>
    );
  }
}
SimpleMatrix.defaultProps = {
	data:[
      {
        country: 'Street Type',
        'Street Type': 1,
        'Street TypeColor': 'hsl(22, 70%, 50%)',
        'Max Speed': 0.47,
        'Max SpeedColor': 'hsl(284, 70%, 50%)',
        Location: 0.11,
        LocationColor: 'hsl(208, 70%, 50%)',
        'Average Speed': 0.3,
        'Average SpeedColor': 'hsl(282, 70%, 50%)',
        Daylight: 0.07,
        DaylightColor: 'hsl(198, 70%, 50%)',
        'Day Type': 0.1,
        'Day TypeColor': 'hsl(60, 70%, 50%)',
      },
      {
        country: 'Max Speed',
        'Street Type': 0.47,
        'Street TypeColor': 'hsl(67, 70%, 50%)',
        'Max Speed': 1,
        'Max SpeedColor': 'hsl(204, 70%, 50%)',
        Location: 0.27,
        LocationColor: 'hsl(208, 70%, 50%)',
        'Average Speed': 0.41,
        'Average SpeedColor': 'hsl(21, 70%, 50%)',
        Daylight: 0.08,
        DaylightColor: 'hsl(235, 70%, 50%)',
        'Day Type': 0.12,
        'Day TypeColor': 'hsl(134, 70%, 50%)',
      },
      {
        country: 'Location',
        'Street Type': 0.11,
        'Street TypeColor': 'hsl(208, 70%, 50%)',
        'Max Speed': 0.27,
        'Max SpeedColor': 'hsl(269, 70%, 50%)',
        Location: 1,
        LocationColor: 'hsl(208, 70%, 50%)',
        'Average Speed': 0.35,
        'Average SpeedColor': 'hsl(283, 70%, 50%)',
        Daylight: 0.01,
        DaylightColor: 'hsl(302, 70%, 50%)',
        'Day Type': 0.11,
        'Day TypeColor': 'hsl(140, 70%, 50%)',
      },
      {
        country: 'Average Speed',
        'Street Type': 0.54,
        'hot dogColor': 'hsl(23, 70%, 50%)',
        'Max Speed': 0.41,
        'Max SpeedColor': 'hsl(165, 70%, 50%)',
        Location: 0.35,
        LocationColor: 'hsl(208, 70%, 50%)',
        'Average Speed': 1,
        'Average SpeedColor': 'hsl(258, 70%, 50%)',
        Daylight: 0.11,
        DaylightColor: 'hsl(341, 70%, 50%)',
        'Day Type': 0.35,
        'Day TypeColor': 'hsl(161, 70%, 50%)',
      },
      {
        country: 'Daylight',
        'Street Type': 0.07,
        'hot dogColor': 'hsl(133, 70%, 50%)',
        'Max Speed': 0.08,
        'Max SpeedColor': 'hsl(250, 70%, 50%)',
        Location: 0.01,
        LocationColor: 'hsl(208, 70%, 50%)',
        'Average Speed': 0.11,
        'Average SpeedColor': 'hsl(355, 70%, 50%)',
        Daylight: 1,
        DaylightColor: 'hsl(297, 70%, 50%)',
        'Day Type': 0.07,
        'Day TypeColor': 'hsl(17, 70%, 50%)',
      },
      {
        country: 'Day Type',
        'Street Type': 0.1,
        'Street TypeColor': 'hsl(208, 70%, 50%)',
        'Max Speed': 0.12,
        'Max SpeedColor': 'hsl(269, 70%, 50%)',
        Location: 0.11,
        LocationColor: 'hsl(208, 70%, 50%)',
        'Average Speed': 0.35,
        'Average SpeedColor': 'hsl(283, 70%, 50%)',
        Daylight: 0.07,
        DaylightColor: 'hsl(302, 70%, 50%)',
        'Day Type': 1,
        'Day TypeColor': 'hsl(140, 70%, 50%)',
      },
    ],
	title: 'Korrelation',
	width: '60vw',
	height: '60vh',
};
SimpleMatrix.propTypes = {
  /**
   * Array of strings for the icons that should be displayed
   */
  data: PropTypes.arrayOf(PropTypes.object),
  /**
	 * String for the title
   */
  title: PropTypes.string,
  /**
   * How high the chart is supposed to be can't be 0 else wont render (Logo string for the project name
   */
  height: PropTypes.string,
  /**
   * How wide the chart is supposed to be can't be 0 else wont render (Logo string for the project name
   */
  width: PropTypes.string,
};
