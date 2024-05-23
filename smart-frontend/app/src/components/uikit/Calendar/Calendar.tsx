import { useState, useMemo } from 'react';
import CalendarHeader from './CalendarHeader/CalendarHeader';
import CalendarGrid from './CalendarGrid/CalendarGrid';
import type { MonthSwitchDirections } from './Calendar.types';
import { getCalendarMap } from './Calendar.utils';
import { getToday, addMonths } from '@utils/date/calendarUtils';
import s from './Calendar.module.scss';

export interface CalendarProps {
  date?: Date;
  rangeFrom?: Date;
  rangeTo?: Date;
  onDateClick: (date: Date) => void;
}

const Calendar = ({ date, rangeFrom, rangeTo, onDateClick }: CalendarProps) => {
  const [selectedMonth, setSelectedMonth] = useState(date ?? getToday());

  const handleMonthChange = (direction: MonthSwitchDirections) => () => {
    setSelectedMonth(direction === 'prev' ? addMonths(selectedMonth, -1) : addMonths(selectedMonth, 1));
  };

  const calendarMap = useMemo(() => getCalendarMap(selectedMonth), [selectedMonth]);

  return (
    <div className={s.calendar}>
      <div className={s.calendar__section} data-test="calendar-header">
        <CalendarHeader onMonthChange={handleMonthChange} month={selectedMonth} />
      </div>
      <div className={s.calendar__section} data-test="calendar-days">
        <CalendarGrid
          calendarMap={calendarMap}
          onDateClick={onDateClick}
          rangeFrom={rangeFrom}
          rangeTo={rangeTo}
          selectedDate={date}
          selectedMonth={selectedMonth}
        />
      </div>
    </div>
  );
};

export default Calendar;
