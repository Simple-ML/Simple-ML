import React from 'react';
import  SimpleBarChart from './SimpleBarChart.js';

export default {
  title: 'BarChart',
  component: SimpleBarChart,
};

const Template = (args) => <SimpleBarChart {...args} />;

export const BarChart = Template.bind({});

