import React from 'react';
import  SimpleAppBar from './SimpleAppBar.js';

export default {
  title: 'AppBar',
  component: SimpleAppBar,
};

const Template = (args) => <SimpleAppBar {...args} />;

export const AppBar = Template.bind({});
