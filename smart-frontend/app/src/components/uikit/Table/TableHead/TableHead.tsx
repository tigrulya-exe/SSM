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
import React, { useMemo } from 'react';
import type { TableColumnSchema } from '../Table.types';
import TableRow from '../TableRow/TableRow';
import TableHeadCell from '../TableCell/TableHeadCell';
import { buildHeader } from '../Table.utils';

type TableHeadProps = {
  columns: TableColumnSchema[];
};

const TableHead: React.FC<TableHeadProps> = ({ columns }) => {
  const headerRows = useMemo(() => buildHeader(columns), [columns]);

  return (
    <thead>
      {headerRows.map((row, index) => (
        <TableRow key={index}>
          {row.map(({ column: { schema, ...columnProps }, rowSpan, colSpan }) => (
            <React.Fragment key={columnProps.name}>
              <TableHeadCell {...columnProps} rowSpan={rowSpan} colSpan={colSpan} />
            </React.Fragment>
          ))}
        </TableRow>
      ))}
    </thead>
  );
};

export default TableHead;
