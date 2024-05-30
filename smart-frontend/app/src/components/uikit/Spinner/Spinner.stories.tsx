import { SpinnerPanel } from '@uikit/Spinner/Spinner';
import type { Meta, StoryFn } from '@storybook/react';

const SpinnerStyle = {
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
};

export default {
  title: 'uikit/Spinner',
  component: SpinnerPanel,
  argTypes: {
    size: {
      type: 'number',
      name: 'Size',
      defaultValue: 40,
    },
  },
} as Meta<typeof SpinnerPanel>;

const Template: StoryFn<typeof SpinnerPanel> = (args) => {
  return (
    <div style={SpinnerStyle}>
      <SpinnerPanel {...args} />
    </div>
  );
};

export const SpinnerElement = Template.bind({});
