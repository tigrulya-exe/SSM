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
import { addHours, addDays, addMonths, getToday, addWeeks, addMinutes } from '@utils/date/calendarUtils';
import type { DateRange, DynamicDateRange, StaticDateRange, SerializedDate } from '@models/dateRange';
import { isDynamicDateRange, isStaticDateRange } from '@models/dateRange';
import { parseValueSegments } from '@utils/unitUtils';
import { deserializeDate, serializeDate } from '@utils/date/dateConvertUtils';

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

  if (isStringify) return serializeDateRange(staticRange) as StaticDateRange<T>;

  return staticRange as StaticDateRange<T>;
};

/**
 * Converted dateRange (dynamicRange (as string) and staticRange (as two instances of Date))
 * to string (for dynamicRange - without changes)
 * and two numbers (for staticRange - from,to are seconds)
 */
export const serializeDateRange = (dateRange: DateRange): DateRange<SerializedDate> => {
  if (isDynamicDateRange(dateRange)) return dateRange;

  if (!isStaticDateRange(dateRange)) {
    throw new Error('Something wrong: timeRange not object and not string');
  }

  return {
    from: serializeDate(dateRange.from),
    to: serializeDate(dateRange.to),
  };
};

/**
 * Converted dateRange with from,to as number (seconds) to instances of Date
 */
export const deserializeDateRange = (dateRange: DateRange<SerializedDate>): DateRange => {
  if (isDynamicDateRange<SerializedDate>(dateRange)) return dateRange;

  if (!isStaticDateRange<SerializedDate>(dateRange)) {
    throw new Error('Something wrong: timeRange not object and not string');
  }

  return {
    from: deserializeDate(dateRange.from),
    to: deserializeDate(dateRange.to),
  };
};
