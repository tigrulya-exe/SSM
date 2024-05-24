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
