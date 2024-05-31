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
import React, { Fragment } from 'react';
import { useTableContext } from '../TableContext';
import type { TableColumnSchema, TableCellValueSchema } from '../Table.types';
import { SchemaColumnType } from '../Table.types';
import TableCell from './TableCell';
import { getValueByPath } from '@utils/objectUtils';
import {
  //
  BigTextCell,
  DateTimeCell,
  DurationCell,
  MemoryCell,
  PercentCell,
  StatusTextCell,
} from './AdvancedCells';
import { orElseGet } from '@utils/checkUtils';

interface TableCellsRendererProps<T extends object> {
  model: T;
}

const columnsIterator = <T extends object>(columns: TableColumnSchema[], model: T): React.ReactElement[] => {
  return columns.map(({ name, schema, subColumns }) => {
    if (subColumns && subColumns.length > 0) {
      return <Fragment key={name}>{columnsIterator<T>(subColumns, model)}</Fragment>;
    }
    return <TableCellRenderer model={model} name={name} schema={schema} key={name} />;
  });
};

const TableCellsRenderer = <T extends object>({ model }: TableCellsRendererProps<T>) => {
  const { columns } = useTableContext();
  const hasColumns = columns && columns.length > 0;

  return <>{hasColumns && columnsIterator<T>(columns, model)}</>;
};

export default TableCellsRenderer;

interface TableCellRendererProps<T extends object> {
  model: T;
  name: string;
  schema?: TableCellValueSchema;
}

const TableCellRenderer = <T extends object>({ model, name, schema }: TableCellRendererProps<T>) => {
  const { type, cellRenderer } = schema ?? {};

  if (cellRenderer) {
    return <>{cellRenderer(model)}</>;
  }

  const fieldValue = getValueByPath(model, name) as React.ReactNode;

  if (type === SchemaColumnType.BigText) {
    return <BigTextCell value={fieldValue as string} data-qa={name} />;
  }

  if (type === SchemaColumnType.DateTime) {
    return <DateTimeCell value={fieldValue as number} data-qa={name} />;
  }

  if (type === SchemaColumnType.Duration) {
    return <DurationCell value={fieldValue as number} data-qa={name} />;
  }

  if (type === SchemaColumnType.Memory) {
    return <MemoryCell value={fieldValue as number} data-qa={name} />;
  }

  if (type === SchemaColumnType.Percent) {
    return <PercentCell value={fieldValue as number} data-qa={name} />;
  }

  if (type === SchemaColumnType.StatusText) {
    return <StatusTextCell value={fieldValue as string} data-qa={name} />;
  }

  return <TableCell data-qa={name}>{orElseGet(fieldValue, String)}</TableCell>;
};
