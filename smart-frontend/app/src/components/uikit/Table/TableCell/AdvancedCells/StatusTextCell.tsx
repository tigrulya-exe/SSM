import React from 'react';
import TableCell from '@uikit/Table/TableCell/TableCell';
import { orElseGet } from '@utils/checkUtils';
import { getStatusLabel } from '@utils/humanisationUtils';
import type { AdvancedCellsProps } from './AdvancedCells.types';

const StatusTextCell = ({ value, ...props }: AdvancedCellsProps<string>) => {
  return <TableCell {...props}>{orElseGet(value, getStatusLabel)}</TableCell>;
};

export default StatusTextCell;
