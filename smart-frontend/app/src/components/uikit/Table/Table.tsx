import React, { useMemo } from 'react';
import cn from 'classnames';

import TableHead from './TableHead/TableHead';
import Spinner from '@uikit/Spinner/Spinner';
import EmptyRow from './TableRow/EmptyRow';
import type { TableColumnSchema } from './Table.types';

import s from './Table.module.scss';
import type { TableContextOptions } from './TableContext';
import { TableContext } from './TableContext';
import type { EmptyTableFilter, FilterParams, SortingProps } from '@models/table';

type TableVariants = 'primary' | 'secondary' | 'clear';

export interface TableProps<Filter extends EmptyTableFilter = EmptyTableFilter>
  extends React.HTMLAttributes<HTMLDivElement>,
    Partial<SortingProps>,
    Partial<FilterParams<Filter>> {
  columns?: TableColumnSchema[];
  children: React.ReactNode;
  variant?: TableVariants;
  isLoading?: boolean;
  spinner?: React.ReactNode;
  noData?: React.ReactNode;
  width?: string;
  dataTest?: string;
}

const defaultEmptyRowLength = 100;

const Table = <Filter extends EmptyTableFilter = EmptyTableFilter>({
  className,
  children,
  columns,
  sortParams,
  onSorting,
  isLoading = false,
  variant = 'primary',
  spinner = <TableSpinner />,
  noData = <TableNoData />,
  width,
  dataTest = 'table',
  filter,
  onFiltering,
  ...props
}: TableProps<Filter>) => {
  const tableClasses = cn(s.table, s[`table_${variant}`]);
  const contextData = useMemo(
    () => ({ sortParams, onSorting, filter, onFiltering, columns }),
    [sortParams, onSorting, filter, onFiltering, columns],
  ) as TableContextOptions<EmptyTableFilter>;

  return (
    <div className={cn(className, s.tableWrapper, 'scroll')} {...props} data-test={dataTest}>
      <TableContext.Provider value={contextData}>
        <table className={tableClasses} style={{ width: width }}>
          {columns?.length && <TableHead columns={columns} />}
          <TableBody>
            {isLoading && (
              <EmptyRow data-test="loading" columnCount={defaultEmptyRowLength}>
                {spinner}
              </EmptyRow>
            )}
            {!isLoading && children}
            {!isLoading && (
              <EmptyRow
                //
                columnCount={defaultEmptyRowLength}
                className={s.table__row_noData}
                data-test="no-data"
              >
                {noData}
              </EmptyRow>
            )}
          </TableBody>
        </table>
      </TableContext.Provider>
    </div>
  );
};

export default Table;

const TableBody: React.FC<React.HTMLProps<HTMLTableSectionElement>> = ({ children }) => {
  return <tbody>{children}</tbody>;
};

const TableNoData = () => <span className={s.table__textNoData}>No data</span>;
const TableSpinner = () => (
  <div className={s.table__spinnerWrapper}>
    <Spinner />
  </div>
);
