import React, { useRef, useState, useCallback } from 'react';
import type { TableRowProps } from './TableRow';
import TableRow from './TableRow';
import cn from 'classnames';
import t from '../Table.module.scss';
import { useResizeObserver } from '@hooks/useResizeObserver';
import Collapse from '@uikit/Collapse/Collapse';
import { useTableContext } from '@uikit/Table/TableContext';

export interface ExpandableRowProps extends TableRowProps {
  isExpanded: boolean;
  expandedContent?: React.ReactNode;
  className?: string;
  expandedClassName?: string;
}

const ExpandableRow = ({
  children,
  isExpanded,
  expandedContent = undefined,
  className = '',
  expandedClassName = '',
  ...props
}: ExpandableRowProps) => {
  const { columns } = useTableContext<Record<string, unknown>>();
  const [rowWidth, setRowWidth] = useState(0);
  const refRow = useRef<HTMLTableRowElement>(null);

  const rowClasses = cn(className, t.expandableRowMain, {
    'is-open': isExpanded,
  });

  const expandedRowClasses = cn(t.expandableRowContent, expandedClassName);

  const setRowNewWidth = useCallback(() => {
    if (!refRow.current) return;
    const parent = refRow.current.closest(`.${t.tableWrapper}`) as HTMLDivElement;
    setRowWidth(parent ? parent.offsetWidth : refRow.current.offsetWidth);
  }, []);

  useResizeObserver(refRow, setRowNewWidth);

  return (
    <>
      <TableRow ref={refRow} className={rowClasses} {...props}>
        {children}
      </TableRow>
      {expandedContent && (
        <tr className={expandedRowClasses}>
          <td colSpan={columns?.length ?? 100}>
            <div style={{ width: `${rowWidth}px` }}>
              <Collapse isExpanded={isExpanded}>
                <div className={t.expandableRowContent__inner}>{expandedContent}</div>
              </Collapse>
            </div>
          </td>
        </tr>
      )}
    </>
  );
};

export default ExpandableRow;
