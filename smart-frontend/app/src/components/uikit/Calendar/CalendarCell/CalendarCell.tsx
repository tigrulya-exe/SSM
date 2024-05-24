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
import { getDayClasses } from './CalendarCell.utils';
import { getDate, getMonth } from '@utils/date/calendarUtils';

export interface CalendarDayProps {
  dayId: string;
  day: Date;
  selectedMonth: Date;
  selectedDate?: Date;
  startDate?: Date;
  endDate?: Date;
  onClick: React.MouseEventHandler<HTMLButtonElement>;
}

const CalendarDay = ({ dayId, day, selectedDate, selectedMonth, onClick, startDate, endDate }: CalendarDayProps) => {
  const dayClassNames = getDayClasses({
    day,
    selectedMonth,
    selectedDate,
    startDate,
    endDate,
  });

  const isBtnDisabled = getMonth(day) !== getMonth(selectedMonth);

  return (
    <button tabIndex={-1} className={dayClassNames} onClick={onClick} data-day-id={dayId} disabled={isBtnDisabled}>
      {getDate(day)}
    </button>
  );
};

export default CalendarDay;
