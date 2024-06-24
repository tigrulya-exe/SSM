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
import Table from '@uikit/Table/Table';
import { auditEventsColumns } from './AuditEventsTable.schema';
import { useDispatch, useStore } from '@hooks';
import { isShowSpinner } from '@uikit/Table/Table.utils';
import TableRow from '@uikit/Table/TableRow/TableRow';
import TableCellsRenderer from '@uikit/Table/TableCell/TableCellsRenderer';
import { setAuditEventsFilter, setAuditEventsSortParams } from '@store/adh/auditEvents/auditEventsTableSlice';
import type { SortParams } from '@models/table';
import type { AdhAuditEventsFilter } from '@models/adh';

const AuditEventsTable: React.FC = () => {
  const dispatch = useDispatch();
  const auditEvents = useStore(({ adh }) => adh.auditEvents.auditEvents);
  const isLoading = useStore(({ adh }) => isShowSpinner(adh.auditEvents.loadState));

  const filter = useStore(({ adh }) => adh.auditEventsTable.filter);
  const sortParams = useStore(({ adh }) => adh.auditEventsTable.sortParams);

  const handleFiltering = (filter: Partial<AdhAuditEventsFilter>) => {
    dispatch(setAuditEventsFilter(filter));
  };
  const handleSorting = (sortParams: SortParams) => {
    dispatch(setAuditEventsSortParams(sortParams));
  };

  return (
    <Table
      isLoading={isLoading}
      columns={auditEventsColumns}
      filter={filter}
      onFiltering={handleFiltering}
      sortParams={sortParams}
      onSorting={handleSorting}
    >
      {auditEvents.map((auditEvent) => (
        <TableRow key={auditEvent.id}>
          <TableCellsRenderer model={auditEvent} />
        </TableRow>
      ))}
    </Table>
  );
};

export default AuditEventsTable;
