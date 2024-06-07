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
import type { EmptyTableFilter, PaginationParams, SortParams } from '@models/table';

type ExecuteWithMinDelayArgs = {
  startDate: Date;
  delay: number;
  callback: () => void;
};

export const executeWithMinDelay = ({ startDate, delay, callback }: ExecuteWithMinDelayArgs) => {
  const curDate = new Date();
  const dateDiff = curDate.getTime() - startDate.getTime();
  const timerId = setTimeout(
    () => {
      callback();
    },
    Math.max(delay - dateDiff, 0),
  );

  return () => {
    clearTimeout(timerId);
  };
};

export const prepareLimitOffset = (paginationParams: PaginationParams) => {
  return {
    offset: paginationParams.pageNumber * paginationParams.perPage,
    limit: paginationParams.perPage,
  };
};

export const prepareSorting = ({ sortBy, sortDirection }: SortParams) => {
  return {
    sortBy,
    sortOrder: sortDirection.toUpperCase(),
  };
};

export const clearFilter = <F extends EmptyTableFilter>(filter: F) => {
  return Object.entries(filter).reduce(
    (res, [filterName, filterValue]) => {
      const isEmptyString = filterValue === '';
      const isEmptyArray = Array.isArray(filterValue) && filterValue.length === 0;

      if (!isEmptyString && !isEmptyArray) {
        res[filterName as keyof F] = filterValue;
      }
      return res;
    },
    {} as Record<keyof F, unknown>,
  );
};

export const prepareQueryParams = <F extends EmptyTableFilter>(
  filter?: F,
  sortParams?: SortParams,
  paginationParams?: PaginationParams,
) => {
  const filterPart = filter ? clearFilter(filter) : {};
  const sortingPart = sortParams ? prepareSorting(sortParams) : {};
  const paginationPart = paginationParams ? prepareLimitOffset(paginationParams) : {};

  return {
    ...filterPart,
    ...sortingPart,
    ...paginationPart,
  };
};

export const absFilter = <T>(list: T[]) => (list.length ? list : undefined);
