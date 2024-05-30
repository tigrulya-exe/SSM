import React from 'react';
import TableCell from '@uikit/Table/TableCell/TableCell';
import { orElseGet } from '@utils/checkUtils';
import { showPercent } from '@utils/convertUtils';
import type { AdvancedCellsProps } from './AdvancedCells.types';

const PercentCell = ({ value, ...props }: AdvancedCellsProps<number>) => {
  return <TableCell {...props}>{orElseGet(value, showPercent)}</TableCell>;
};

export default PercentCell;
