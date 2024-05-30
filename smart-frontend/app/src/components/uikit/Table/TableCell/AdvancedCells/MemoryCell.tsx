import React from 'react';
import TableCell from '@uikit/Table/TableCell/TableCell';
import { orElseGet } from '@utils/checkUtils';
import { bytesConversion } from '@utils/convertUtils';
import type { AdvancedCellsProps } from './AdvancedCells.types';

const MemoryCell = ({ value, ...props }: AdvancedCellsProps<number>) => {
  return <TableCell {...props}>{orElseGet(value, bytesConversion)}</TableCell>;
};

export default MemoryCell;
