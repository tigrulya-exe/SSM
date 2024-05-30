import React from 'react';
import { orElseGet } from '@utils/checkUtils';
import TableCell from '@uikit/Table/TableCell/TableCell';
import { dateToString } from '@utils/date/dateConvertUtils';
import type { AdvancedCellsProps } from './AdvancedCells.types';

const prepareDate = (value: number) => {
  return dateToString(new Date(value), { toUtc: true });
};

const DateTimeCell = ({ value, ...props }: AdvancedCellsProps<number>) => {
  return (
    <TableCell width="180px" minWidth="125px" {...props}>
      {orElseGet(value, prepareDate)}
    </TableCell>
  );
};

export default DateTimeCell;
