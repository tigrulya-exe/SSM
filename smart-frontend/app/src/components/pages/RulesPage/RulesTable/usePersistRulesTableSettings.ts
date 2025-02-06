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
