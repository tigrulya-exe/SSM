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
import type { SelectOption, SingleSelectOptions } from '@uikit/Select/Select.types';
import type { ReactNode } from 'react';
import React, { useContext } from 'react';

export type SingleSelectContextOptions<T> = SingleSelectOptions<T> & {
  setOptions: (list: SelectOption<T>[]) => void;
  originalOptions: SelectOption<T>[];
  renderItem?: (model: SelectOption<T>) => ReactNode;
};

export const SingleSelectContext = React.createContext<SingleSelectContextOptions<unknown>>(
  {} as SingleSelectContextOptions<unknown>,
);

export const useSingleSelectContext = <T>() => {
  const ctx = useContext<SingleSelectContextOptions<T>>(
    SingleSelectContext as React.Context<SingleSelectContextOptions<T>>,
  );
  if (!ctx) {
    throw new Error('useContext must be inside a Provider with a value');
  }
  return ctx;
};
