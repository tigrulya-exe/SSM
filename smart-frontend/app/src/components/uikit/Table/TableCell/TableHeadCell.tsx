import React, { useRef } from 'react';
import type { TableColumnSchema } from '../Table.types';
import type { TableCellProps } from './TableCell';
import TableCell from './TableCell';
import SortingLabel from '@uikit/SortingLabel/SortingLabel';
import { useTableContext } from '../TableContext';
import ConditionalWrapper from '@uikit/ConditionalWrapper/ConditionalWrapper';
import TableFilter from '../TableFilter/TableFilter';
import cn from 'classnames';
import s from '../Table.module.scss';
import { isValidData } from '@utils/checkUtils';

export type TableHeadCellProps = Omit<TableColumnSchema, 'schema'> & Omit<TableCellProps, 'align' | 'tag' | 'children'>;

const TableHeadCell: React.FC<TableHeadCellProps> = ({
  label,
  headerAlign,
  name,
  isSortable = false,
  filterRenderer,
  className,
  subColumns,
  filterName,
  ...props
}) => {
  const ref = useRef(null);
  const { sortParams, onSorting, filter } = useTableContext<Record<string, unknown>>();
  // if a getHasSetFilter is described in the column config then try to use this function, else try to detect by field name
  const hasSetFilter = isValidData(filter?.[filterName ?? name]);
  const thClasses = cn(className, s.tableHeaderCell, {
    [s.tableHeaderCell_hasSetFilter]: hasSetFilter,
    [s.tableHeaderCell_hasSubColumns]: !!subColumns?.length,
  });

  return (
    <TableCell data-test={name} {...props} align={headerAlign} tag="th" className={thClasses} ref={ref}>
      <ConditionalWrapper
        Component={SortingLabel}
        isWrap={isSortable}
        name={name}
        sortParams={sortParams}
        onSorting={onSorting}
      >
        {label}
      </ConditionalWrapper>
      {filterRenderer && <TableFilter filterRenderer={filterRenderer} hasSetFilter={hasSetFilter} thRef={ref} />}
    </TableCell>
  );
};

export default TableHeadCell;
