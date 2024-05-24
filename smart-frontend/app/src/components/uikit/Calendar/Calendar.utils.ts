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
import {
  addDays,
  eachDayOfInterval,
  endOfMonth,
  getDay,
  getWeekOfMonth,
  isMonday,
  startOfDay,
  startOfMonth,
} from '@utils/date/calendarUtils';
import type { CalendarMap } from './Calendar.types';

const getActiveMonthInterval = (dateFromProps: Date) =>
  eachDayOfInterval({ start: startOfMonth(dateFromProps), end: endOfMonth(dateFromProps) });

const fillCalendarMapWithPrevMonthDays = (calendarMap: Date[][], currentFirstDate: Date) => {
  const lastEmptyDay = calendarMap[0].findIndex((item) => !!item);
  for (let i = 0; i < lastEmptyDay; i += 1) {
    calendarMap[0][i] = addDays(currentFirstDate, -(lastEmptyDay - i));
  }
};

const fillCalendarMapWithNextMonthDays = (calendarMap: Date[][], currentLastDate: Date) => {
  const lastCurrentMonthWeek = calendarMap.findIndex((item) => item.length < 7);
  const firstDayOfNextMonth = calendarMap[lastCurrentMonthWeek].length;
  for (let i = lastCurrentMonthWeek, j = firstDayOfNextMonth, k = 1; i < 6; i += 1, j = 0) {
    for (; j < 7; j += 1, k += 1) {
      calendarMap[i][j] = addDays(currentLastDate, k);
    }
  }
};

export const getCalendarMap = (date: Date) => {
  const currentFirstDate = startOfMonth(date);
  const currentLastDate = startOfDay(endOfMonth(date));
  const startsFromMonday = isMonday(currentFirstDate);

  const calendarMap = getActiveMonthInterval(date).reduce(
    (calendarMap: CalendarMap, next) => {
      const day = getDay(next) === 0 ? 6 : getDay(next) - 1;
      const week = getWeekOfMonth(next, { weekStartsOn: 1 }) - 1;
      calendarMap[week][day] = next;
      return calendarMap;
    },
    [[], [], [], [], [], []],
  );

  if (!startsFromMonday) {
    fillCalendarMapWithPrevMonthDays(calendarMap, currentFirstDate);
  }

  fillCalendarMapWithNextMonthDays(calendarMap, currentLastDate);

  return calendarMap;
};
