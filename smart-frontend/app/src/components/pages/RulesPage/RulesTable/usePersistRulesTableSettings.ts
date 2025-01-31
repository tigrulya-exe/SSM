import { useDispatch, usePersistSettings, useStore } from '@hooks';
import { mergePaginationParams } from '@hooks/usePersistSettings';
import { setRulesFilter, setRulesPaginationParams, setRulesSortParams } from '@store/adh/rules/rulesTableSlice';
import type { AdhRuleFilter } from '@models/adh';

const mergeFilters = (filterFromStorage: AdhRuleFilter, actualFilter: AdhRuleFilter): AdhRuleFilter => {
  if (!filterFromStorage) {
    return actualFilter;
  }

  const result: AdhRuleFilter = {
    ...actualFilter,
    ...filterFromStorage,
  };

  return result;
};

export const usePersistRulesTableSettings = () => {
  const dispatch = useDispatch();

  const filter = useStore(({ adh }) => adh.rulesTable.filter);
  const sortParams = useStore(({ adh }) => adh.rulesTable.sortParams);
  const paginationParams = useStore(({ adh }) => adh.rulesTable.paginationParams);
  const { perPage } = paginationParams;

  usePersistSettings(
    {
      localStorageKey: 'adh/rulesTable',
      settings: {
        sortParams,
        perPage,
        paginationParams,
        filter,
      },
      isReadyToLoad: true,
      onSettingsLoaded: (settings) => {
        const mergedFilter = mergeFilters(settings.filter, filter);
        dispatch(setRulesFilter(mergedFilter));
        dispatch(setRulesSortParams(settings.sortParams));
        dispatch(setRulesPaginationParams(mergePaginationParams(settings.perPage, paginationParams)));
      },
    },
    [paginationParams, perPage, sortParams, filter],
  );
};
