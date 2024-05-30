import React, { useContext } from 'react';
import type { TableColumnSchema } from './Table.types';
import type { EmptyTableFilter, FilterParams, SortingProps } from '@models/table';

export type TableContextOptions<Filter extends EmptyTableFilter> = Partial<SortingProps> &
  Partial<FilterParams<Filter>> & { columns?: TableColumnSchema[] };

export const TableContext = React.createContext<TableContextOptions<EmptyTableFilter>>(
  {} as TableContextOptions<EmptyTableFilter>,
);

// eslint-disable-next-line react-refresh/only-export-components
export const useTableContext = <T extends EmptyTableFilter>(): TableContextOptions<T> => {
  const ctx = useContext<TableContextOptions<T>>(TableContext as React.Context<TableContextOptions<T>>);
  if (!ctx) {
    throw new Error('useContext must be inside a Provider with a value');
  }
  return ctx;
};
