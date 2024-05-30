import React from 'react';
import cn from 'classnames';
import s from '../Table.module.scss';
import type { AlignType } from '../Table.types';

export interface TableCellProps extends Omit<React.TdHTMLAttributes<HTMLTableCellElement>, 'align'> {
  align?: AlignType;
  tag?: 'th' | 'td';
  width?: string;
  minWidth?: string;
  isMultilineText?: boolean;
  hasIconOnly?: boolean;
}

const TableCell = React.forwardRef<HTMLTableCellElement, TableCellProps>(
  ({ tag = 'td', align, width, minWidth, className, children, style, isMultilineText = false, ...props }, ref) => {
    const Tag = tag;
    const cellClasses = cn(className, s.tableCell, {
      [s.tableCell_oneLine]: !isMultilineText,
      [s[`tableCell_align-${align}`]]: align,
    });

    return (
      <Tag ref={ref} className={cellClasses} {...props} style={{ width, minWidth, ...style }}>
        <div className={s.tableCell__inner}>{children}</div>
      </Tag>
    );
  },
);

export default TableCell;
