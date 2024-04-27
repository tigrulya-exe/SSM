import type { StoryFn, Meta } from '@storybook/react';
import PasswordInput from './PasswordInput';

export default {
  title: 'uikit/PasswordInput',
  component: PasswordInput,
  argTypes: {
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
} as Meta<typeof PasswordInput>;

const Template: StoryFn<typeof PasswordInput> = (args) => {
  return (
    <div style={{ padding: '40px' }}>
      <PasswordInput {...args} />
    </div>
  );
};

export const PasswordInputElement = Template.bind({
  size: 'medium',
  variant: 'primary',
});
