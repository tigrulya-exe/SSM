import React, { useState } from 'react';
import type { Meta, StoryObj } from '@storybook/react';
import MultiSelect from '@uikit/Select/MultiSelect/MultiSelect';

type Story = StoryObj<typeof MultiSelect>;

export default {
  title: 'uikit/Select',
  argTypes: {
    isSearchable: {
      control: { type: 'boolean' },
    },
    checkAllLabel: {
      control: { type: 'text' },
    },
    maxHeight: {
      control: { type: 'number' },
    },
    placeholder: {
      control: { type: 'text' },
    },
  },
  component: MultiSelect,
} as Meta<typeof MultiSelect>;

const options = [
  {
    value: 123,
    label: 'Host 123',
  },
  {
    value: 234,
    label: 'Host 234',
  },
  {
    value: 345,
    label: 'Host 345',
  },
  {
    value: 456,
    label: 'Host 456',
  },
  {
    value: 567,
    label: 'Host 567',
  },
  {
    value: 678,
    label: 'Host 678',
  },
  {
    value: 789,
    label: 'Host 789',
  },
];

type MultiSelectExampleProps = {
  isSearchable?: boolean;
  checkAllLabel?: string;
  searchPlaceholder?: string;
  maxHeight?: number;
  placeholder?: string;
};

const MultiSelectExample: React.FC<MultiSelectExampleProps> = ({
  isSearchable,
  checkAllLabel,
  searchPlaceholder,
  placeholder,
  maxHeight,
}) => {
  const [value, setValue] = useState<number[]>([]);

  return (
    <div style={{ padding: 30, maxWidth: 300 }}>
      <MultiSelect
        value={value}
        onChange={setValue}
        options={options}
        isSearchable={isSearchable}
        checkAllLabel={checkAllLabel}
        searchPlaceholder={searchPlaceholder}
        maxHeight={maxHeight}
        placeholder={placeholder}
        style={{ maxWidth: 300 }}
      />
    </div>
  );
};

export const MultiSelectEasy: Story = {
  args: {
    isSearchable: false,
    checkAllLabel: undefined,
    searchPlaceholder: 'Search hosts',
    placeholder: 'All hosts',
  },
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  render: ({ isSearchable, checkAllLabel, searchPlaceholder, maxHeight, placeholder }) => {
    return (
      <MultiSelectExample
        isSearchable={isSearchable}
        checkAllLabel={checkAllLabel}
        searchPlaceholder={searchPlaceholder}
        maxHeight={maxHeight}
        placeholder={placeholder}
      />
    );
  },
};
