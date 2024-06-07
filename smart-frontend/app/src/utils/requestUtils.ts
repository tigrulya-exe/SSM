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
