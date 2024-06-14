import { useDebounce, useDispatch, useRequestTimer, useStore } from '@hooks';
import { useEffect } from 'react';
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

  const debounceGetData = useDebounce(() => {
    dispatch(getRules());
  }, defaultDebounceDelay);

  const debounceRefreshData = useDebounce(() => {
    dispatch(refreshRules());
  }, defaultDebounceDelay);

  useRequestTimer(debounceGetData, debounceRefreshData, requestFrequency, true, [
    filter,
    sortParams,
    paginationParams,
    requestFrequency,
  ]);
};
