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
/**
 * date time in seconds, use for send request to Api, and save values to local storage
 */
export type SerializedDate = number;

/**
 * By default, `from` and `to` instances of Date  (new Date())
 */
export type StaticDateRange<T = Date> = {
  from: T;
  to: T;
};

export type DynamicDateRange =
  | 'now-1h'
  | 'now-2h'
  | 'now-4h'
  | 'now-8h'
  | 'now-12h'
  | 'now-24h'
  | 'now-2d'
  | 'now-5d'
  | 'now-7d'
  | 'now-14d'
  | 'now-1M';

export type DateRange<T = Date> = StaticDateRange<T> | DynamicDateRange;

export const isDynamicDateRange = <T = Date>(range?: DateRange<T>): range is DynamicDateRange => {
  return typeof range === 'string';
};

export const isStaticDateRange = <T = Date>(range?: DateRange<T>): range is StaticDateRange<T> => {
  return typeof range === 'object';
};
