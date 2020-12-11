import React from 'react';
import  Icons from './Icons'
import { withDesign } from 'storybook-addon-designs'
export default {
  title: 'Components/Icon',
  component: Icons,
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
		}
	}
};
const Template = (args) => <Icons {...args} />;

export const Navigation = Template.bind({});
