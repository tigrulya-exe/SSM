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
import { forwardRef } from 'react';
import type { TableRowProps } from './TableRow';
import TableRow from './TableRow';
import TableCell from '../TableCell/TableCell';

export interface EmptyRowProps extends TableRowProps {
  columnCount?: number;
}

const EmptyRow = forwardRef<HTMLTableRowElement, EmptyRowProps>(({ children, columnCount = 100, ...props }, ref) => {
  return (
    <TableRow ref={ref} {...props}>
      <TableCell colSpan={columnCount} align="center">
        {children}
      </TableCell>
    </TableRow>
  );
});

export default EmptyRow;
