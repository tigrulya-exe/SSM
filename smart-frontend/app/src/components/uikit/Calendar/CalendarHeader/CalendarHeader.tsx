import IconButton from '@uikit/IconButton/IconButton';
import { getMonth, getYear } from '@utils/date/calendarUtils';
import type { ChangeMonthHandler } from '../Calendar.types';
import { monthsDictionary } from '../Calendar.constants';
import s from './CalendarHeader.module.scss';
import cn from 'classnames';

interface CalendarHeaderProps {
  month: Date;
  onMonthChange: ChangeMonthHandler;
}

const CalendarHeader = ({ month, onMonthChange }: CalendarHeaderProps) => (
  <div className={s.calendarHeader}>
    <IconButton
      icon="chevron"
      tabIndex={-1}
      onClick={onMonthChange('prev')}
      className={cn(s.monthButton, s.prevMonth)}
      variant="secondary"
      size={12}
    />
    <div className={s.currentMonth}>{`${monthsDictionary[getMonth(month)]} ${getYear(month)}`}</div>
    <IconButton
      icon="chevron"
      tabIndex={-1}
      onClick={onMonthChange('next')}
      className={cn(s.monthButton, s.nextMonth)}
      variant="secondary"
      size={12}
    />
  </div>
);

export default CalendarHeader;
