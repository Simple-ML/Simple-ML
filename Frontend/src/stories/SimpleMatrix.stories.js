import React from 'react';
import  SimpleMatrix from './SimpleMatrix.js';

export default {
  title: 'Matrix',
  component: SimpleMatrix,
};

const Template = (args) => <SimpleMatrix {...args} />;

export const Matrix = Template.bind({});

