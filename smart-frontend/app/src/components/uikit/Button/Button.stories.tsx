import type { Meta, StoryObj } from '@storybook/react';
import Button from './Button';
import { fn } from '@storybook/test';

const meta = {
  title: 'uikit/Button',
  component: Button,
  parameters: {
    // Optional parameter to center the component in the Canvas. More info: https://storybook.js.org/docs/configure/story-layout
    layout: 'centered',
  },
  argTypes: {
    disabled: {
      description: 'Disabled',
      control: { type: 'boolean' },
    },
    type: {
      table: {
        disable: true,
      },
    },
    size: {
      description: 'Size',
      defaultValue: 'medium',
      options: ['medium', 'small'],
      control: { type: 'radio' },
    },
    variant: {
      description: 'Variant',
      defaultValue: 'primary',
      options: ['primary', 'secondary'],
      control: { type: 'radio' },
    },
  },
} as Meta<typeof Button>;

export default meta;

type Story = StoryObj<typeof meta>;

export const ButtonEasy: Story = {
  args: {
    children: 'Create connection',
    variant: 'primary',
    size: 'medium',
    hasError: false,
    onClick: fn(),
  },
};

export const ButtonWithIcon: Story = {
  args: {
    children: 'Some button label',
    variant: 'primary',
    size: 'medium',
    hasError: false,
    onClick: fn(),
  },
};
