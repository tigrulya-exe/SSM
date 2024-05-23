import { addHours, addDays, addMonths, getToday, addWeeks, addMinutes } from '@utils/date/calendarUtils';
import type { DateRange, DynamicDateRange, StaticDateRange, SerializedDate } from '@models/dateRange';
import { isDynamicDateRange, isStaticDateRange } from '@models/dateRange';
import { parseValueSegments } from '@utils/unitUtils';
import { dateParse, dateStringify } from '@utils/date/dateConvertUtils';

const allowDateTimeUnits = ['m', 'h', 'd', 'w', 'M'] as const;
type DateTimeUnit = (typeof allowDateTimeUnits)[number];
type DateTimeSegment = {
  value: number;
  unit: DateTimeUnit;
};
const dateTimeIncrementor: Record<DateTimeUnit, (date: Date | number, amount: number) => Date> = {
  m: addMinutes,
  h: addHours,
  d: addDays,
  w: addWeeks,
  M: addMonths,
};

/**
 * template of DynamicDateRange is `now-{count}{DateTimeUnit}`
 */
const parseDynamicRange = (dateRange: DynamicDateRange): DateTimeSegment[] => {
  const dateTimeRecord = dateRange.split('now-').at(-1);
  if (!dateTimeRecord) {
    throw new Error(`Invalid dateRange format: ${dateRange}`);
  }

  const delimiter = ' ';
  const separatedDateTimeRange = dateTimeRecord.replace(/(\d+)/g, `${delimiter}$1`).substring(1);
  const segments = parseValueSegments(separatedDateTimeRange, delimiter) as DateTimeSegment[];

  segments.forEach(({ unit }) => {
    if (!allowDateTimeUnits.includes(unit)) {
      throw new Error(`Invalid dateRange unit: "${unit}" in "${dateRange}"`);
    }
  });

  return segments;
};

/**
 * Parse dynamicRange string, calc `from` date, and return `{ from, to }`
 */
export const getRangeFromNow = (dateRange: DynamicDateRange): StaticDateRange => {
  const now = getToday();

  const segments = parseDynamicRange(dateRange);

  const from = segments.reduce((from, { value, unit }) => {
    const fromCb = dateTimeIncrementor[unit];

    return fromCb(from, -value);
  }, now);

  return {
    from,
    to: now,
  };
};

export const convertToStaticRange = <T = Date>(dateRange: DateRange<T>, isStringify: boolean): StaticDateRange<T> => {
  if (isStaticDateRange<T>(dateRange)) return dateRange;

  if (!isDynamicDateRange<T>(dateRange)) {
    throw new Error('Something wrong: timeRange not object and not string');
  }

  // in this place staticRange, always StaticDateRange<Date>
  const staticRange = getRangeFromNow(dateRange);

  if (isStringify) return dateRangeStringify(staticRange) as StaticDateRange<T>;

  return staticRange as StaticDateRange<T>;
};

/**
 * Converted dateRange (dynamicRange (as string) and staticRange (as two instances of Date))
 * to string (for dynamicRange - without changes)
 * and two numbers (for staticRange - from,to are seconds)
 */
export const dateRangeStringify = (dateRange: DateRange): DateRange<SerializedDate> => {
  if (isDynamicDateRange(dateRange)) return dateRange;

  if (!isStaticDateRange(dateRange)) {
    throw new Error('Something wrong: timeRange not object and not string');
  }

  return {
    from: dateStringify(dateRange.from),
    to: dateStringify(dateRange.to),
  };
};

/**
 * Converted dateRange with from,to as number (seconds) to instances of Date
 */
export const dateRangeParse = (dateRange: DateRange<SerializedDate>): DateRange => {
  if (isDynamicDateRange<SerializedDate>(dateRange)) return dateRange;

  if (!isStaticDateRange<SerializedDate>(dateRange)) {
    throw new Error('Something wrong: timeRange not object and not string');
  }

  return {
    from: dateParse(dateRange.from),
    to: dateParse(dateRange.to),
  };
};
