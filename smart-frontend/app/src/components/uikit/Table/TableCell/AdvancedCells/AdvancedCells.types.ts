import type { TableCellProps } from '@uikit/Table/TableCell/TableCell';

export interface AdvancedCellsProps<T> extends Omit<TableCellProps, 'children'> {
  value?: T;
}
