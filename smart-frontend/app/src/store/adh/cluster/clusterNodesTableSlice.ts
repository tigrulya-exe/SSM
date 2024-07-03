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
import type { TableState } from '@models/table';
import { createTableSlice } from '@store/redux';
import type { AdhClusterNodesFilter } from '@models/adh';

type AdhTablesTableState = TableState<AdhClusterNodesFilter>;

const createInitialState = (): AdhTablesTableState => ({
  filter: {
    id: undefined,
    clusterStates: undefined,
    registrationTime: undefined,
    status: undefined,
  },
  paginationParams: {
    perPage: 10,
    pageNumber: 0,
  },
  requestFrequency: 0,
  sortParams: {
    sortBy: 'id',
    sortDirection: 'asc',
  },
});

const clusterNodesTableSlice = createTableSlice({
  name: 'adh/clusterNodesTable',
  createInitialState,
  reducers: {},
  extraReducers: () => {},
});

const {
  //
  setPaginationParams: setClusterNodesPaginationParams,
  cleanupTable: cleanupClusterNodesTable,
  setSortParams: setClusterNodesSortParams,
  setFilter: setClusterNodesFilter,
  resetFilter: resetClusterNodesFilter,
} = clusterNodesTableSlice.actions;

export {
  //
  setClusterNodesPaginationParams,
  cleanupClusterNodesTable,
  setClusterNodesSortParams,
  setClusterNodesFilter,
  resetClusterNodesFilter,
};
export default clusterNodesTableSlice.reducer;
