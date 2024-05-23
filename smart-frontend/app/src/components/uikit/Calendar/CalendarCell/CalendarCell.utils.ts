import { isEqual } from '@utils/date';
import { getMonth, startOfDay, isDateInRange } from '@utils/date/calendarUtils';
import s from './CalendarCell.module.scss';
import cn from 'classnames';

interface GetDayClassesProps {
  day: Date;
  selectedMonth: Date;
  selectedDate?: Date;
  startDate?: Date;
  endDate?: Date;
}

export const getDayClasses = ({ day, selectedMonth, selectedDate, startDate, endDate }: GetDayClassesProps) => {
  const isDayInThisMonth = getMonth(day) === getMonth(selectedMonth);
  const isToday = selectedDate && isEqual(startOfDay(day), startOfDay(selectedDate));

  const isDayStartDate = startDate && isEqual(startOfDay(day), startOfDay(startDate));
  const isDayEndDate = endDate && isEqual(startOfDay(day), startOfDay(endDate));

  const isDaySelected = selectedDate && isEqual(startOfDay(day), startOfDay(selectedDate));
  const isDisabled = !isDayInThisMonth && !isToday && !isDaySelected;

  const isInRange =
    startDate &&
    endDate &&
    isDateInRange(day, startDate, endDate) &&
    isDayInThisMonth &&
    !isDayStartDate &&
    !isDayEndDate;

  const isSelectedDate = isDayInThisMonth && (isDaySelected || isDayStartDate || isDayEndDate);

  return cn(s.calendarCell, {
    [s.calendarCell__disabled]: isDisabled,
    [s.calendarCell__selectedDate]: isSelectedDate,
    [s.calendarCell__today]: isToday && !isDaySelected,
    [s.calendarCell__thisMonth]: isDayInThisMonth && !isToday && !isDaySelected,
    [s.calendarCell__inSelectedRange]: isInRange,
    [s.calendarCell__startRangeDate]: isSelectedDate && isDayStartDate,
    [s.calendarCell__endRangeDate]: isSelectedDate && isDayEndDate,
  });
};
