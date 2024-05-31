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
import React from 'react';
import type { SingleSelectPanelProps } from '@uikit/Select/SingleSelect/SingleSelectPanel/SingleSelectPanel';
import SingleSelectPanel from '@uikit/Select/SingleSelect/SingleSelectPanel/SingleSelectPanel';
import PopoverPanelDefault from '@uikit/Popover/PopoverPanelDefault/PopoverPanelDefault';
import type { EmptyTableFilter } from '@models/table';
import { useFieldFilter } from '@uikit/Table/TableFilter/useFieldFilter';

interface TableSingleSelectFilterProps<FilterConfig extends EmptyTableFilter, T>
  extends Omit<SingleSelectPanelProps<T>, 'value' | 'onChange'> {
  filterName: keyof FilterConfig;
  closeFilter: () => void;
}

const TableSingleSelectFilter = <FilterConfig extends EmptyTableFilter, T>({
  noneLabel = 'Clear filter',
  filterName,
  closeFilter,
  ...props
}: TableSingleSelectFilterProps<FilterConfig, T>) => {
  const [filterValue, setFilterValue] = useFieldFilter<FilterConfig, T>(filterName);

  const handleChange = (val: T | null) => {
    setFilterValue(val ?? undefined);
    closeFilter();
  };

  return (
    <PopoverPanelDefault>
      <SingleSelectPanel {...props} noneLabel={noneLabel} value={filterValue ?? null} onChange={handleChange} />
    </PopoverPanelDefault>
  );
};

export default TableSingleSelectFilter;
