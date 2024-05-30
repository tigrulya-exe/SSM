import type { EmptyTableFilter } from '@models/table';
import { useTableContext } from '@uikit/Table/TableContext';

export const useFieldFilter = <Filter extends EmptyTableFilter, Value>(filterName: keyof Filter) => {
  const { filter, onFiltering } = useTableContext<Filter>();

  const setFilterValue = (value?: Value) => onFiltering?.({ ...filter, [filterName]: value } as Filter);

  const filterValue = filter?.[filterName] as Value;

  return [filterValue, setFilterValue] as const;
};
