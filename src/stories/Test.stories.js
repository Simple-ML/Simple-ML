import React from 'react';
import { withDesign } from 'storybook-addon-designs'
import  Button  from './Test';

export default {
  title: 'Test',
  component: Button,
	decorators: [withDesign],
	parameters: {
			backgrounds: {
				default: 'violet',
				values: [
					{ name: 'dark', value: '#461f4c' },
					{ name: 'light', value: 'white' },
				],
			},
		design: {
			type: 'figma',
			url:
				'https://www.figma.com/file/QMvF0qb7diOxDsPYIfvjSg/Design-MVP-2.0?node-id=8%3A6',
		},
	},
  argTypes: {
    backgroundColor: { control: 'color' },
  },
};

const Template = (args) => <Button {...args} />;

export const Primary= Template.bind({});
Primary.args = {
  primary: true,
  label: 'Button',
};

