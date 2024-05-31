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
import type { Nullable } from '@models/utils';

type GetCallback<T, R> = (data: T) => R;

export const isValidData = <T>(data: T | undefined | null): data is T =>
  !(Number.isNaN(data) || typeof data === 'undefined' || data === null);

export const orElseGet = <T, R>(
  data: Nullable<T>,
  callback?: GetCallback<T, R> | null,
  placeholder: R = '-' as R,
): R => {
  if (!isValidData(data) || data === '') return placeholder as R;

  return (callback ? callback(data) : data) as R;
};

export const isPromiseFulfilled = <T>(p: PromiseSettledResult<T>): p is PromiseFulfilledResult<T> =>
  p.status === 'fulfilled';
export const isPromiseRejected = <T>(p: PromiseSettledResult<T>): p is PromiseRejectedResult => p.status === 'rejected';

export const fulfilledFilter = <T>(list: PromiseSettledResult<T>[]) =>
  list.filter(isPromiseFulfilled).map(({ value }) => value);

export const rejectedFilter = <T>(list: PromiseSettledResult<T>[]) =>
  list.filter(isPromiseRejected).map(({ reason }) => reason);

export const isNumber = (value?: number | string) => {
  return value !== null && value !== '' && !Number.isNaN(Number(value));
};
