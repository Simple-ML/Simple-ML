import React from 'react';
import  SimpleHistogramChart from './SimpleHistogramChart.js';

export default {
  title: 'HistogramChart',
  component: SimpleHistogramChart,
};

const Template = (args) => <SimpleHistogramChart {...args} />;

export const HistogramChart = Template.bind({});

