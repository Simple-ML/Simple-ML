import React, {useState, useEffect} from 'react';
import {ResponsiveBar} from '@nivo/bar';
import PropTypes from 'prop-types';
//import dataset from '../data/streets_corrupted_shuffled_1000_profile';
//let myData = dataset.attributes[2];
//let data;
//let state;
//export default class SimpleBarChart extends React.Component {
  //constructor(props) {
    //super(props);
    //this.state = {
      //dataIsReady: false,
    //};
  //}
  //componentDidMount() {
    //fetch('data/streets_corrupted_shuffled_1000_profile.json', {
      //headers: {
        //'Content-Type': 'application/json',
        //Accept: 'application/json',
      //},
    //})
      //.then(function (response) {
        //console.log(response);
        //return response.json();
      //})
      //.then(function (myJson) {
        //data = myJson;
        //console.log(myJson);
        //this.setState({
          //dataIsReady: true,
          //data: myJson,
        //});
      //});
  //}
  //render() {
    //const {dataIsReady, data} = this.state;
    //if (dataIsReady) {
      //return <div>{JSON.stringify(data)}</div>;
    //} else {
      //return <h1> is loading </h1>;
    //}
  //}
//}
export default class SimpleBarChart extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      error: null,
      isLoaded: false,
      items:[] 
    };
  }

  componentDidMount() {
    fetch(this.props.url)
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
            isLoaded: true,
            items: result
          });
        },
        // Note: it's important to handle errors here
        // instead of a catch() block so that we don't swallow
        // exceptions from actual bugs in components.
        (error) => {
          this.setState({
            isLoaded: true,
            error
          });
        }
      )
  }

  render() {
    const { error, isLoaded, items } = this.state;
    if (error) {
      return <div>Error: {error.message}</div>;
    } else if (!isLoaded) {
      return <div>Loading...</div>;
    } else {
			console.log(items);
		return ( 
			<div>
			<div className="App">
			<h1> { this.props.title }</h1>
			<div style={{width:this.props.width,height:this.props.height}}>
		<ResponsiveBar
				data={ items.attributes[this.props.number].statistics.class_distribution}	
				keys={['number_of_instances']}
				indexBy="class"
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
				borderColor={{ from: 'color', modifiers: [ [ 'darker', 1.6 ] ] }}
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
				labelTextColor={{ from: 'color', modifiers: [ [ 'darker', 1.6 ] ] }}
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
}
			//<div>{JSON.stringify(items)}</div>
SimpleBarChart.defaultProps = {
	width: '60vw',
	height: '60vh',
	title: 'Wochentag',
	url: 'data/streets_corrupted_shuffled_1000_profile.json',
	number: 2
};
SimpleBarChart.propTypes = {
  /**
   * Url where the data is being pulled from
   */
  url: PropTypes.string,
  /**
	 * String for thWochentage title
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
  /**
   * Which of the provided data from the given array should be used
   */
  number: PropTypes.oneOf([1, 2,9, 11]),
};
