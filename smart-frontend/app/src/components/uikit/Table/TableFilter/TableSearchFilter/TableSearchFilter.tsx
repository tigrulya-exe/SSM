import React, { useState } from 'react';
import { useDebounce } from '@hooks';
import { defaultDebounceDelay } from '@constants';
import { useFieldFilter } from '../useFieldFilter';
import type { EmptyTableFilter } from '@models/table';
import PopoverPanelDefault from '@uikit/Popover/PopoverPanelDefault/PopoverPanelDefault';
import type { SearchInputProps } from '@uikit/SearchInput/SearchInput';
import SearchInput from '@uikit/SearchInput/SearchInput';

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
    <PopoverPanelDefault>
      <SearchInput {...props} value={localValue ?? ''} onChange={handleChange} denyCharsPattern={denyCharsPattern} />
    </PopoverPanelDefault>
  );
};

export default TableSearchFilter;
