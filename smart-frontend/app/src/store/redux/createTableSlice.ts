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
import type {
  SliceCaseReducers,
  ValidateSliceCaseReducers,
  PayloadAction,
  ActionReducerMapBuilder,
  Draft,
} from '@reduxjs/toolkit';
import { createSlice } from '@reduxjs/toolkit';

import type { PaginationParams, SortParams, TableState } from '@models/table';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
type ExtractFilter<S> = S extends TableState<infer F, infer E> ? F : never;
// eslint-disable-next-line @typescript-eslint/no-unused-vars
type ExtractEntity<S> = S extends TableState<infer F, infer E> ? E : never;

type ExtractSortParams<S> = SortParams<ExtractEntity<S>>;

interface CreateTableSliceOptions<
  S extends TableState<ExtractFilter<S>, ExtractEntity<S>>,
  CR extends SliceCaseReducers<S>,
  Name extends string = string,
> {
  name: Name;
  createInitialState: () => S;
  reducers: ValidateSliceCaseReducers<S, CR>;
  extraReducers?: (builder: ActionReducerMapBuilder<S>) => void;
}

export function createTableSlice<
  S extends TableState<ExtractFilter<S>, ExtractEntity<S>>,
  CR extends SliceCaseReducers<S>,
>(options: CreateTableSliceOptions<S, CR>) {
  const { name, createInitialState, reducers, extraReducers } = options;

  return createSlice({
    name,
    initialState: createInitialState(),
    reducers: {
      ...reducers,
      setFilter(state, action: PayloadAction<Partial<ExtractFilter<S>>>) {
        state.filter = {
          ...state.filter,
          ...action.payload,
        };
        state.paginationParams.pageNumber = 0;
      },
      resetFilter(state) {
        const initialData = createInitialState();
        state.filter = initialData.filter as Draft<ExtractFilter<S>>;
      },
      setPaginationParams(state, action: PayloadAction<PaginationParams>) {
        state.paginationParams = action.payload;
      },
      setSortParams(state, action: PayloadAction<ExtractSortParams<S>>) {
        state.sortParams = action.payload as Draft<ExtractSortParams<S>>;
      },
      resetSortParams(state) {
        state.sortParams = createInitialState().sortParams as Draft<ExtractSortParams<S>>;
      },
      setRequestFrequency(state, action: PayloadAction<number>) {
        state.requestFrequency = action.payload;
      },
      // method for combine reset table's setting
      resetSettings(state) {
        const initialData = createInitialState();
        state.filter = initialData.filter as Draft<ExtractFilter<S>>;
        state.sortParams = initialData.sortParams as Draft<ExtractSortParams<S>>;
        state.paginationParams = initialData.paginationParams as Draft<PaginationParams>;
      },
      cleanupTable() {
        return createInitialState();
      },
    },
    extraReducers,
  });
}
