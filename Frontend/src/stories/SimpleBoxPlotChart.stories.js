import React from 'react';
import SimpleBoxPlotChart from './SimpleBoxPlotChart.js';

export default {
  title: 'BoxPlotChart',
  component: SimpleBoxPlotChart,
};

const Template = (args) => <SimpleBoxPlotChart {...args} />;

export const BoxPlotChart = Template.bind({});

