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
