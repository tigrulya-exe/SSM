import {
  compareAsc,
  addSeconds,
  addMinutes,
  addHours,
  addDays,
  addMonths,
  addWeeks,
  addYears,
  getDay,
  getDate,
  getWeekOfMonth,
  getMonth,
  getYear,
  startOfDay,
  startOfMonth,
  endOfDay,
  endOfMonth,
  isMonday,
  isBefore,
  eachDayOfInterval,
} from 'date-fns';
import { isEqual } from '@utils/date/index';

const getToday = () => {
  const localToday = new Date();

  return localToday;
};

const isDateLessThan = (date: Date, minDate?: Date) => !!minDate && compareAsc(minDate, date) === 1;

const isDateBiggerThan = (date: Date, maxDate?: Date) => !!maxDate && compareAsc(date, maxDate) === 1;

const isDateInRange = (date: Date, minDate?: Date, maxDate?: Date) => {
  const isBiggerThenStart = minDate ? isDateBiggerThan(date, minDate) || isEqual(date, minDate) : true;
  const isLessThenEnd = maxDate ? isDateLessThan(date, maxDate) || isEqual(date, maxDate) : true;

  return isBiggerThenStart && isLessThenEnd;
};

export {
  compareAsc,
  addSeconds,
  addMinutes,
  addHours,
  addDays,
  addMonths,
  addWeeks,
  addYears,
  getDay,
  getDate,
  getWeekOfMonth,
  getMonth,
  getYear,
  startOfDay,
  startOfMonth,
  endOfDay,
  endOfMonth,
  isMonday,
  isBefore,
  eachDayOfInterval,
  getToday,
  isDateLessThan,
  isDateBiggerThan,
  isDateInRange,
};
