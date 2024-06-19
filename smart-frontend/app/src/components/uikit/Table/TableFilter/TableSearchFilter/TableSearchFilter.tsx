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
import React, { useState } from 'react';
import { useDebounce } from '@hooks';
import { defaultDebounceDelay } from '@constants';
import { useFieldFilter } from '../useFieldFilter';
import type { EmptyTableFilter } from '@models/table';
import PopoverPanelDefault from '@uikit/Popover/PopoverPanelDefault/PopoverPanelDefault';
import type { SearchInputProps } from '@uikit/SearchInput/SearchInput';
import SearchInput from '@uikit/SearchInput/SearchInput';
import s from './TableSearchFilter.module.scss';

export interface TableSearchFilterProps<FilterConfig extends EmptyTableFilter>
  extends Omit<SearchInputProps, 'value' | 'onChange'> {
  filterName: keyof FilterConfig;
}

const TableSearchFilter = <FilterConfig extends EmptyTableFilter>({
  denyCharsPattern,
  filterName,
  ...props
}: TableSearchFilterProps<FilterConfig>) => {
  const [filterValue, setFilterValue] = useFieldFilter<FilterConfig, string>(filterName);
  const [localValue, setLocalValue] = useState<string | undefined>(filterValue);

  const debounceChange = useDebounce((val: string) => {
    setFilterValue(val || undefined);
  }, defaultDebounceDelay);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;
    setLocalValue(value);
    debounceChange(value);
  };

  return (
    <PopoverPanelDefault className={s.tableSearchFilter}>
      <SearchInput {...props} value={localValue ?? ''} onChange={handleChange} denyCharsPattern={denyCharsPattern} />
    </PopoverPanelDefault>
  );
};

export default TableSearchFilter;
