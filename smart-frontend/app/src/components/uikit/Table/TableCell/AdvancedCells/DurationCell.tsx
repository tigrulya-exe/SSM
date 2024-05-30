import React from 'react';
import TableCell from '@uikit/Table/TableCell/TableCell';
import { orElseGet } from '@utils/checkUtils';
import { secondsToDuration } from '@utils/date/dateConvertUtils';
import type { AdvancedCellsProps } from './AdvancedCells.types';

const DurationCell = ({ value, ...props }: AdvancedCellsProps<number>) => {
  return <TableCell {...props}>{orElseGet(value, secondsToDuration)}</TableCell>;
};

export default DurationCell;
