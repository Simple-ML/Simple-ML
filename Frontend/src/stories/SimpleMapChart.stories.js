import React from 'react';
import SimpleMapChart from './SimpleMapChart.js';

export default {
  title: 'MapChart',
  component: SimpleMapChart,
};

const Template = (args) => <SimpleMapChart {...args} />;

export const MapChart = Template.bind({});

