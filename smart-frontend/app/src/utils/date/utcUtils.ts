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
import type { StaticDateRange } from '@models/dateRange';

export const localDateToUtc = (localDate: Date) => {
  const timestamp = localDate.getTime();
  const offset = localDate.getTimezoneOffset() * 60 * 1000; // minutes * 60sec * 1000ms

  return new Date(timestamp + offset);
};

export const utcDateToLocal = (utcDate: Date) => {
  const timestamp = utcDate.getTime();
  const offset = utcDate.getTimezoneOffset() * 60 * 1000; // minutes * 60sec * 1000ms

  return new Date(timestamp - offset);
};

export const utcStaticDateRange = (staticRange: StaticDateRange): StaticDateRange => {
  const { from, to } = staticRange;
  return {
    from: utcDateToLocal(from),
    to: utcDateToLocal(to),
  };
};
