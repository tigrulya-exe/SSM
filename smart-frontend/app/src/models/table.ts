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
export type SortDirection = 'asc' | 'desc';

export interface SortParams<T extends string = string> {
  sortBy: T;
  sortDirection: SortDirection;
}

export interface PaginationParams {
  pageNumber: number;
  perPage: number;
}

// eslint-disable-next-line @typescript-eslint/ban-types
export type EmptyTableFilter = {};

export type TableState<F extends EmptyTableFilter, E extends string = string> = {
  filter: F;
  sortParams: SortParams<E>;
  paginationParams: PaginationParams;
  requestFrequency: number;
};

export interface SortingProps<T extends string = string> {
  sortParams: SortParams<T>;
  onSorting: (sortParams: SortParams<T>) => void;
}

export interface FilterParams<Filter extends Record<string, unknown>> {
  filter: Filter;
  onFiltering: (filter: Filter) => void;
}
