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
