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
import type { EmptyTableFilter } from '@models/table';
import PopoverPanelDefault from '@uikit/Popover/PopoverPanelDefault/PopoverPanelDefault';
import DateRangePickerPanel from '@uikit/DateRangePicker/DateRangePickerPanel/DateRangePickerPanel';
import { useFieldFilter } from '@uikit/Table/TableFilter/useFieldFilter';
import type { SerializedDate, DateRange } from '@models/dateRange';
import { dateRangeParse, dateRangeStringify } from '@utils/date/dateRangeUtils';

interface TableDateRangePickerFilterProps<FilterConfig extends EmptyTableFilter> {
  filterName: keyof FilterConfig;
  closeFilter: () => void;
}

const TableDateRangePickerFilter = <FilterConfig extends EmptyTableFilter>({
  filterName,
  closeFilter,
}: TableDateRangePickerFilterProps<FilterConfig>) => {
  const [filterValue, setFilterValue] = useFieldFilter<FilterConfig, DateRange<SerializedDate>>(filterName);

  // DateRangePickerPanel work with from,to instance of Date. But in filter (and in Slice) we save safe number (seconds) value
  const localFilterValue = useMemo(() => {
    return filterValue ? dateRangeParse(filterValue) : 'now-1h';
  }, [filterValue]);

  const handleApply = (value: DateRange) => {
    const storeSafeValue = dateRangeStringify(value);
    setFilterValue(storeSafeValue);
    closeFilter();
  };

  return (
    <PopoverPanelDefault>
      <DateRangePickerPanel range={localFilterValue} onApply={handleApply} onRevert={closeFilter} />
    </PopoverPanelDefault>
  );
};

export default TableDateRangePickerFilter;
