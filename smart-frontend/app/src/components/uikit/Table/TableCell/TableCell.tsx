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
