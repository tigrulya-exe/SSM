import InputDate from './InputDate';
import type { Meta, StoryObj } from '@storybook/react';
import { useEffect, useState } from 'react';

type Story = StoryObj<typeof InputDate>;

export default {
  title: 'uikit/InputDate',
  component: InputDate,
  argTypes: {
    placeholder: {
      defaultValue: 'Select date...',
      description: 'Placeholder',
      control: { type: 'text' },
    },
    value: {
      defaultValue: new Date(),
      description: 'value',
      control: { type: 'date' },
    },
  },
} as Meta<typeof InputDate>;

export const InputDateStory: Story = {
  args: {
    value: new Date(),
  },
  render: ({ value, ...args }) => {
    const [localValue, setLocalValue] = useState<Date | undefined>(value);

    useEffect(() => {
      setLocalValue(value);
    }, [value]);

    return (
      <div style={{ width: 600, display: 'flex', alignItems: 'center' }}>
        <InputDate {...args} value={localValue} onChange={setLocalValue} />
      </div>
    );
  },
};

export const EmptyInputDateStory: Story = {
  args: {
    value: undefined,
  },
  render: ({ value, ...args }) => {
    const [localValue, setLocalValue] = useState<Date | undefined>(value);

    useEffect(() => {
      setLocalValue(value);
    }, [value]);

    return (
      <div style={{ width: 600, display: 'flex', alignItems: 'center' }}>
        <InputDate {...args} value={localValue} onChange={setLocalValue} />
      </div>
    );
  },
};
