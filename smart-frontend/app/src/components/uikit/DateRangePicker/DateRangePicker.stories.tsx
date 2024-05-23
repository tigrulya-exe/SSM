import { useState, useRef } from 'react';
import type { DateRangePickerProps } from './DateRangePicker';
import DateRangePicker from './DateRangePicker';
import type { Meta, StoryObj } from '@storybook/react';
import { MemoryRouter } from 'react-router-dom';
import { formatDate } from './DateRangePicker.utils';
import { useRequestTimer, useDebounce } from '@hooks';
import { convertToStaticRange } from '@utils/date/dateRangeUtils';
import type { DateRange } from '@models/dateRange';

type Story = StoryObj<typeof DateRangePicker>;

export default {
  title: 'uikit/DateRangePicker',
  component: DateRangePicker,
  decorators: [
    (Story) => {
      return (
        <MemoryRouter initialIndex={0}>
          <Story />
        </MemoryRouter>
      );
    },
  ],
  argTypes: {
    placeholder: {
      defaultValue: 'Select date...',
      description: 'Placeholder',
      control: { type: 'text' },
    },
  },
} as Meta<typeof DateRangePicker>;

const DateRangePickerExample = ({ onApply, ...args }: DateRangePickerProps) => {
  const [localRange, setLocalRange] = useState<DateRange | undefined>(undefined);
  const ref = useRef<HTMLSpanElement>(null);

  const handleSubmit = (range: DateRange) => {
    setLocalRange(range);
    onApply && onApply(range);
  };

  const debouncePrint = useDebounce(() => {
    if (localRange && ref.current) {
      const { from, to } = convertToStaticRange(localRange, false);

      ref.current.innerText = `${formatDate(from)} - ${formatDate(to)}`;
    }
  }, 300);

  useRequestTimer(debouncePrint, debouncePrint, 1, true, [localRange]);

  return (
    <div style={{ width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'start' }}>
      <DateRangePicker {...args} onApply={handleSubmit} range={localRange} />
      <div>
        <span ref={ref}></span>
      </div>
    </div>
  );
};

export const DateRangePickerStory: Story = {
  args: {
    disabled: false,
  },
  render: ({ onApply, ...args }) => {
    return <DateRangePickerExample onApply={onApply} {...args} />;
  },
};
