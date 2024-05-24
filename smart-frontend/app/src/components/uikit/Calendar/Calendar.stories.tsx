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
import Calendar from './Calendar';
import type { Meta, StoryObj } from '@storybook/react';
import { useEffect, useState } from 'react';
import { addDays, endOfDay, isBefore, startOfDay } from '@utils/date/calendarUtils';

type Story = StoryObj<typeof Calendar>;

const now = new Date();

export default {
  title: 'uikit/Calendar',
  component: Calendar,
  argTypes: {
    value: {
      defaultValue: now,
      description: 'Value',
      control: { type: 'date' },
    },
    rangeFrom: {
      defaultValue: now,
      description: 'Range from',
      control: { type: 'date' },
    },
    rangeTo: {
      defaultValue: addDays(now, 7),
      description: 'Range to',
      control: { type: 'date' },
    },
  },
} as Meta<typeof Calendar>;

export const CalendarStory: Story = {
  args: {
    date: now,
  },
  render: ({ date, onDateClick, ...args }) => {
    const [localDate, setLocalDate] = useState<Date | undefined>(date);

    useEffect(() => {
      setLocalDate(date);
    }, [date]);

    const handleDateClick = (date: Date) => {
      setLocalDate(date);
      onDateClick && onDateClick(date);
    };

    return (
      <div style={{ width: 300, display: 'flex', alignItems: 'center' }}>
        <Calendar {...args} onDateClick={handleDateClick} date={localDate} />
      </div>
    );
  },
};

export const CalendarRangeStory: Story = {
  args: {
    rangeFrom: now,
    rangeTo: addDays(now, 7),
  },
  render: ({ rangeFrom, rangeTo, onDateClick, ...args }) => {
    const [localDate, setLocalDate] = useState<Date | undefined>(undefined);
    const [localRangeFrom, setLocalRangeFrom] = useState(rangeFrom);
    const [localRangeTo, setLocalRangeTo] = useState(rangeTo);

    useEffect(() => {
      if (localRangeFrom && localRangeTo) {
        // swap dates when From > To
        if (localRangeFrom > localRangeTo) {
          setLocalRangeFrom(localRangeTo);
          setLocalRangeTo(localRangeFrom);
        }
      }
    }, [localRangeFrom, localRangeTo]);

    const handleDateClick = (date: Date) => {
      if (localRangeTo !== undefined) {
        setLocalRangeFrom(startOfDay(date));
        setLocalRangeTo(undefined);
        setLocalDate(date);
        return;
      }

      if (localRangeFrom === undefined) return;

      if (isBefore(date, localRangeFrom)) {
        setLocalRangeTo(localRangeFrom);
        setLocalRangeFrom(startOfDay(date));
      } else {
        setLocalRangeTo(endOfDay(date));
      }

      setLocalDate(date);
    };

    return (
      <div style={{ width: 300, display: 'flex', alignItems: 'center' }}>
        <Calendar
          {...args}
          onDateClick={handleDateClick}
          date={localDate}
          rangeFrom={localRangeFrom}
          rangeTo={localRangeTo}
        />
      </div>
    );
  },
};
