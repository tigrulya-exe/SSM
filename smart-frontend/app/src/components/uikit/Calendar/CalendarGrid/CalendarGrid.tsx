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
import CalendarCell from '../CalendarCell/CalendarCell';
import type { CalendarMap } from '../Calendar.types';
import s from './CalendarGrid.module.scss';

interface CalendarGridProps {
  calendarMap: CalendarMap;
  selectedDate?: Date;
  selectedMonth: Date;
  rangeFrom?: Date;
  rangeTo?: Date;
  onDateClick: (date: Date) => void;
}

const CalendarGrid = ({
  calendarMap,
  selectedDate,
  selectedMonth,
  rangeFrom,
  rangeTo,
  onDateClick,
}: CalendarGridProps) => {
  const daysIds: Record<string, Date> = {};

  const handleDayClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    if (e.currentTarget.dataset.dayId) {
      const date: Date = daysIds[e.currentTarget.dataset.dayId];
      onDateClick(date);
    }
  };

  return (
    <div className={s.calendarGrid}>
      {calendarMap.map((week, index) =>
        week.map((day) => {
          const dayId = `${index}${day}`;
          daysIds[dayId] = day;

          return (
            <CalendarCell
              dayId={dayId}
              key={dayId}
              day={day}
              selectedDate={selectedDate}
              selectedMonth={selectedMonth}
              startDate={rangeFrom}
              endDate={rangeTo}
              onClick={handleDayClick}
            />
          );
        }),
      )}
    </div>
  );
};

export default CalendarGrid;
