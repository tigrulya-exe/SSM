import React, { useRef, useState, useEffect, useCallback } from 'react';
import Collapse from '@uikit/Collapse/Collapse';
import type { TableRowProps } from './TableRow';
import TableRow from './TableRow';
import cn from 'classnames';
import s from './ExpandableRow.module.scss';
import tableStyles from '../Table.module.scss';
import { useResizeObserver } from '@hooks/useResizeObserver';

export interface ExpandableRowProps extends TableRowProps {
  isExpanded: boolean;
  expandedContent?: React.ReactNode;
  colSpan: number;
  className?: string;
  expandedClassName?: string;
}

const ExpandableRow = ({
  children,
  isExpanded,
  expandedContent = undefined,
  colSpan,
  className = '',
  expandedClassName = '',
  ...props
}: ExpandableRowProps) => {
  const [isMainHovered, setIsMainHovered] = useState(false);
  const [isExpandHovered, setIsExpandHovered] = useState(false);
  const [rowWidth, setRowWidth] = useState(0);
  const refRow = useRef<HTMLTableRowElement>(null);

  const rowClasses = cn(className, s.expandableRowMain, {
    [tableStyles.hovered]: isMainHovered || isExpandHovered,
    [s.expanded]: isExpanded,
  });

  const expandedRowClasses = cn(s.expandableRowContent, expandedClassName, {
    [tableStyles.hovered]: isMainHovered || isExpandHovered,
  });

  const onMainMouseEnterHandler = () => setIsMainHovered(true);
  const onMainMouseLeaveHandler = () => setIsMainHovered(false);

  const setRowNewWidth = useCallback(() => {
    if (!refRow.current) return;
    setRowWidth(refRow.current.offsetWidth);
  }, []);

  useEffect(() => {
    if (!refRow.current) return;
    const ref = refRow.current;
    ref.addEventListener('mouseenter', onMainMouseEnterHandler);
    ref.addEventListener('mouseleave', onMainMouseLeaveHandler);

    return () => {
      if (!ref) return;
      ref.removeEventListener('mouseenter', onMainMouseEnterHandler);
      ref.removeEventListener('mouseleave', onMainMouseLeaveHandler);
    };
  }, [refRow]);

  useResizeObserver(refRow, setRowNewWidth);

  return (
    <>
      <TableRow ref={refRow} className={rowClasses} {...props}>
        {children}
      </TableRow>
      {expandedContent && isExpanded && (
        <tr
          className={expandedRowClasses}
          onMouseEnter={() => setIsExpandHovered(true)}
          onMouseLeave={() => setIsExpandHovered(false)}
        >
          <td colSpan={colSpan}>
            <div style={{ width: `${rowWidth}px` }}>
              <Collapse isExpanded={true}>
                <div className={s.expandableRowContent_wrapper}>{expandedContent}</div>
              </Collapse>
            </div>
          </td>
        </tr>
      )}
    </>
  );
};

export default ExpandableRow;
