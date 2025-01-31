import { useDispatch, usePersistSettings, useStore } from '@hooks';
import { mergePaginationParams } from '@hooks/usePersistSettings';
import {
  setActionsFilter,
  setActionsPaginationParams,
  setActionsSortParams,
} from '@store/adh/actions/actionsTableSlice';
import type { AdhActionsFilter } from '@models/adh';

const mergeFilters = (filterFromStorage: AdhActionsFilter, actualFilter: AdhActionsFilter): AdhActionsFilter => {
  if (!filterFromStorage) {
    return actualFilter;
  }

  const result: AdhActionsFilter = {
    ...actualFilter,
    ...filterFromStorage,
  };

  return result;
};

export const usePersistActionsTableSettings = () => {
  const dispatch = useDispatch();

  const filter = useStore(({ adh }) => adh.actionsTable.filter);
  const sortParams = useStore(({ adh }) => adh.actionsTable.sortParams);
  const paginationParams = useStore(({ adh }) => adh.actionsTable.paginationParams);
  const { perPage } = paginationParams;

  usePersistSettings(
    {
      localStorageKey: 'adh/actionsTable',
      settings: {
        sortParams,
        perPage,
        paginationParams,
        filter,
      },
      isReadyToLoad: true,
      onSettingsLoaded: (settings) => {
        const mergedFilter = mergeFilters(settings.filter, filter);
        dispatch(setActionsFilter(mergedFilter));
        dispatch(setActionsSortParams(settings.sortParams));
        dispatch(setActionsPaginationParams(mergePaginationParams(settings.perPage, paginationParams)));
      },
    },
    [paginationParams, perPage, sortParams, filter],
  );
};
