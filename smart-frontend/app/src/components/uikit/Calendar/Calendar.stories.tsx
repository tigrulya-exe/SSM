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
