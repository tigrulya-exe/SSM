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
