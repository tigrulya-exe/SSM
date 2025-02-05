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
import { useState, useEffect } from 'react';
import { useLocalStorage } from './useLocalStorage';
import type { PaginationParams } from '@models/table';
import { useStore } from './useStore';

type UsePersistSettingsProps<T> = {
  localStorageKey: string;
  settings: T;
  isReadyToLoad: boolean;
  onSettingsLoaded: (settings: T) => void;
};

const getTableSettingKey = (userName: string, tableKey: string) => `${userName}_${tableKey}`;

const mergePaginationParams = (perPage: number | undefined, paginationParams: PaginationParams): PaginationParams => {
  return perPage === undefined
    ? paginationParams
    : {
        ...paginationParams,
        perPage,
      };
};

export const usePersistSettings = <T>(props: UsePersistSettingsProps<T>, deps: unknown[]) => {
  const username = useStore(({ auth }) => auth.username);

  const [isLoaded, setIsLoaded] = useState(false);

  const [dataFromLocalStorage, storeDataToLocalStorage] = useLocalStorage({
    key: getTableSettingKey(username, props.localStorageKey),
    initData: props.settings,
  });

  useEffect(() => {
    if (props.isReadyToLoad && dataFromLocalStorage && !isLoaded) {
      props.onSettingsLoaded(dataFromLocalStorage);
      setIsLoaded(true);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [props.isReadyToLoad, dataFromLocalStorage, isLoaded]);

  useEffect(() => {
    if (isLoaded) {
      storeDataToLocalStorage(props.settings);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, isLoaded]);

  return isLoaded;
};

export { mergePaginationParams };
