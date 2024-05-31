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
import { LoadState } from '@models/loadState';
import type { TableColumnSchema } from './Table.types';

export const isShowSpinner = (loadState: LoadState): loadState is LoadState.Loading | LoadState.NotLoaded =>
  loadState === LoadState.Loading || loadState === LoadState.NotLoaded;

export const getOptionalColumns = (allColumns: TableColumnSchema[]) => {
  return allColumns.filter(({ required }) => !required);
};

export const getVisibleColumns = (allColumns: TableColumnSchema[], selectedColumns: string[] = []) => {
  const selectedColumnsSet = new Set<string>(selectedColumns);

  return allColumns.filter(({ required, name }) => required || selectedColumnsSet.has(name));
};

export const getColumnDepth = (column: TableColumnSchema): number => {
  if (column.subColumns?.length) {
    const subColumnsDepth = column.subColumns.map((x) => getColumnDepth(x));
    return 1 + Math.max(...subColumnsDepth);
  }

  return 1;
};

type CellSpanConfig = {
  rowSpan: number;
  colSpan: number;
  column: TableColumnSchema;
};

const columnsReduce = (rows: CellSpanConfig[][], columns: TableColumnSchema[], rowsMax: number, depth: number) => {
  // we should calculate columns count for correct value colSpan for parent Cell
  let columnsCount = 0;

  columns.forEach((column) => {
    // every column incremented columnsCount
    columnsCount++;

    // by default rowSpan === rowsMax - depth
    let rowSpan = rowsMax - depth;
    let colSpan = 1;

    if (column.subColumns?.length) {
      const subColumnsCount = columnsReduce(rows, column.subColumns, rowsMax, depth + 1);

      // parent cell has colSpan = summary of all child column
      colSpan = subColumnsCount;
      // parent cell has only rowSpan = 1, it's need for beautiful formatting
      rowSpan = 1;

      columnsCount += subColumnsCount - 1;
    }

    rows[depth] = rows[depth] ?? [];
    rows[depth].push({
      rowSpan,
      colSpan,
      column,
    });
  });

  return columnsCount;
};

export const getMaxColumnsDepth = (columns: TableColumnSchema[]) => {
  const columnsDepth = columns.map((x) => getColumnDepth(x));
  return Math.max(...columnsDepth);
};

export const buildHeader = (columns: TableColumnSchema[]) => {
  const maxDepth = getMaxColumnsDepth(columns);

  const rows: CellSpanConfig[][] = [];

  columnsReduce(rows, columns, maxDepth, 0);
  return rows;
};
