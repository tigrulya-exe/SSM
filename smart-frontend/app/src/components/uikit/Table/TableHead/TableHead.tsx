import React, { useMemo } from 'react';
import type { TableColumnSchema } from '../Table.types';
import TableRow from '../TableRow/TableRow';
import TableHeadCell from '../TableCell/TableHeadCell';
import { buildHeader } from '../Table.utils';

type TableHeadProps = {
  columns: TableColumnSchema[];
};

const TableHead: React.FC<TableHeadProps> = ({ columns }) => {
  const headerRows = useMemo(() => buildHeader(columns), [columns]);

  return (
    <thead>
      {headerRows.map((row, index) => (
        <TableRow key={index}>
          {row.map(({ column: { schema, ...columnProps }, rowSpan, colSpan }) => (
            <React.Fragment key={columnProps.name}>
              <TableHeadCell {...columnProps} rowSpan={rowSpan} colSpan={colSpan} />
            </React.Fragment>
          ))}
        </TableRow>
      ))}
    </thead>
  );
};

export default TableHead;
