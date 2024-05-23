import { getToday } from '@utils/date/calendarUtils';

export const getRangeFromNow = (fromCb: (date: Date, value: number) => Date, value: number) => {
  const now = getToday();
  const from = fromCb(now, value);
  return { from, to: now };
};
