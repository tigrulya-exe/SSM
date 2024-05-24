/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
