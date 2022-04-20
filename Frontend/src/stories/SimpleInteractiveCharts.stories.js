import React from 'react';
import SimpleInteractiveCharts from './SimpleInteractiveCharts.js';

export default {
  title: 'InteractiveCharts',
  component: SimpleInteractiveCharts,
};

const Template = (args) => <SimpleInteractiveCharts {...args} />;

export const InteractiveCharts = Template.bind({});

