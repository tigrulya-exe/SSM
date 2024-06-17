import { useDebounce, useDispatch, useRequestTimer, useStore } from '@hooks';
import { useCallback, useEffect } from 'react';
import { cleanupRules, getRules, refreshRules } from '@store/adh/rules/rulesSlice';
import { cleanupRulesTable } from '@store/adh/rules/rulesTableSlice';
import { defaultDebounceDelay } from '@constants';

export const useRequestRules = () => {
  const dispatch = useDispatch();

  const paginationParams = useStore(({ adh }) => adh.rulesTable.paginationParams);
  const sortParams = useStore(({ adh }) => adh.rulesTable.sortParams);
  const filter = useStore(({ adh }) => adh.rulesTable.filter);
  const requestFrequency = useStore(({ adh }) => adh.rulesTable.requestFrequency);

  useEffect(
    () => () => {
      dispatch(cleanupRules());
      dispatch(cleanupRulesTable());
    },
    [dispatch],
  );

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const debounceGetData = useCallback(
    useDebounce(() => {
      dispatch(getRules());
    }, defaultDebounceDelay),
    [],
  );

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const debounceRefreshData = useCallback(
    useDebounce(() => {
      dispatch(refreshRules());
    }, defaultDebounceDelay),
    [],
  );

  useRequestTimer(debounceGetData, debounceRefreshData, requestFrequency, true, [
    filter,
    sortParams,
    paginationParams,
    requestFrequency,
  ]);
};
