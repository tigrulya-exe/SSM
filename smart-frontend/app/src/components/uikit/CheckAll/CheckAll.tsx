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
import React, { useMemo } from 'react';
import Checkbox from '@uikit/Checkbox/Checkbox';

export interface CheckAllProps<T> {
  allList: T[];
  selectedValues: T[] | null;
  onChange: (value: T[]) => void;
  label?: string;
  className?: string;
  disabled?: boolean;
}

const CheckAll = <T,>({ label, allList, selectedValues, onChange, className, disabled }: CheckAllProps<T>) => {
  const isAllChecked = useMemo(() => {
    if (!selectedValues?.length) return false;

    return allList.length === selectedValues.length;
  }, [allList, selectedValues]);

  const handlerAllChanged = (event: React.ChangeEvent<HTMLInputElement>) => {
    onChange?.(event.target.checked ? allList.map((item) => item) : []);
  };

  return (
    <Checkbox
      label={label}
      className={className}
      checked={isAllChecked}
      onChange={handlerAllChanged}
      disabled={disabled}
    />
  );
};

export default CheckAll;
