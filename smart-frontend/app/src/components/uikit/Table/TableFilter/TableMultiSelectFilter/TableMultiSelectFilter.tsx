import React from 'react';
import MultiSelectPanel from '@uikit/Select/MultiSelect/MultiSelectPanel/MultiSelectPanel';
import type { MultiSelectOptions } from '@uikit/Select/Select.types';
import PopoverPanelDefault from '@uikit/Popover/PopoverPanelDefault/PopoverPanelDefault';
import type { EmptyTableFilter } from '@models/table';
import { useFieldFilter } from '@uikit/Table/TableFilter/useFieldFilter';

interface TableMultiSelectFilterProps<FilterConfig extends EmptyTableFilter, T>
  extends Omit<MultiSelectOptions<T>, 'value' | 'onChange'> {
  filterName: keyof FilterConfig;
}

const TableMultiSelectFilter = <FilterConfig extends EmptyTableFilter, T>({
  filterName,
  ...props
}: TableMultiSelectFilterProps<FilterConfig, T>) => {
  const [filterValue, setFilterValue] = useFieldFilter<FilterConfig, T[]>(filterName);

  const handleChange = (val: T[]) => {
    setFilterValue(val?.length ? val : undefined);
  };

  return (
    <PopoverPanelDefault>
      <MultiSelectPanel value={filterValue ?? []} onChange={handleChange} {...props} />
    </PopoverPanelDefault>
  );
};

export default TableMultiSelectFilter;
