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
