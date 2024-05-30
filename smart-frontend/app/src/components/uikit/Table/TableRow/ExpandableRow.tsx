/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
