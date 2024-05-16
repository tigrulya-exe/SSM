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
export type SelectValue = string | number | null;

export interface SelectOption<T = SelectValue> {
  value: T;
  label: string;
  disabled?: boolean;
  title?: string;
}

export interface SingleSelectParams<T> {
  options: SelectOption<T>[];
  value: T | null;
  onChange: (value: T | null) => void;
}

interface CommonSelectParams {
  maxHeight?: number;
  isSearchable?: boolean;
  hasError?: boolean;
  isDisabled?: boolean;
  searchPlaceholder?: string;
}

export interface SingleSelectOptions<T> extends SingleSelectParams<T>, CommonSelectParams {
  noneLabel?: string;
}

export interface MultiPropsParams<T> {
  options: SelectOption<T>[];
  value: T[];
  onChange: (value: T[]) => void;
}

export interface MultiSelectOptions<T> extends MultiPropsParams<T>, CommonSelectParams {
  compactMode?: boolean;
  checkAllLabel?: string;
}
