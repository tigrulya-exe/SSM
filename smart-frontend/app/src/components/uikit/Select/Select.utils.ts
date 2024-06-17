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
import { getStatusLabel } from '@utils/humanisationUtils';
import type { SelectOption } from './Select.types';

export const getOptionsFromEnum = <T extends string, TEnumValue extends string>(someEnum: {
  [key in T]: TEnumValue;
}): SelectOption<TEnumValue>[] => {
  const options = Object.values(someEnum).map((value) => ({
    label: getStatusLabel(value as string),
    value: value as TEnumValue,
  }));

  return options;
};

export const getOptionsFromArray = <T extends string, V = T>(items: T[]): SelectOption<T | V>[] => {
  const options = items.map((value) => ({
    label: getStatusLabel(value),
    value: value,
  }));

  return options;
};
