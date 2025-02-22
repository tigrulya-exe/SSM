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
import type { AdhFileInfoFilter } from '@models/adh';
import type { TableColumnSchema } from '@uikit/Table/Table.types';
import { SchemaColumnType } from '@uikit/Table/Table.types';
import { TableSearchFilter } from '@uikit/Table/TableFilter';

export const hottestFilesColumns: TableColumnSchema[] = [
  {
    name: 'id',
    label: 'ID',
    isSortable: true,
  },
  {
    name: 'path',
    label: 'File path',
    isSortable: true,
    filterRenderer: () => {
      return <TableSearchFilter<AdhFileInfoFilter> filterName="pathLike" placeholder="Search path like" />;
    },
    filterName: 'pathLike',
    schema: {
      type: SchemaColumnType.Text,
    },
  },
  {
    name: 'accessCount',
    label: 'Access count',
    isSortable: true,
    schema: {
      type: SchemaColumnType.Text,
    },
  },
];
