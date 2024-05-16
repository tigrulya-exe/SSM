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
import { useState, useCallback } from 'react';

type StorageProps<T> = {
  key: string;
  initData?: T;
};

type StorageReturnProps<T> = [storageData: T | null, setItem: (itemData: T) => void, removeItem: () => void];

export const useLocalStorage = <T>({ key, initData }: StorageProps<T>): StorageReturnProps<T> => {
  const [storageData, setStorageData] = useState<T | null>(() => {
    const storageData = localStorage.getItem(key) as string;
    try {
      const data = JSON.parse(storageData) as T;
      if (data) return data;
      if (initData) {
        const stringifyData = JSON.stringify(initData);
        localStorage.setItem(key, stringifyData);
        // have to parse stringified data again because getItem doesn't return undefined props from storage to return correct object
        return JSON.parse(stringifyData) as T;
      }
    } catch (e) {
      console.error(e);
    }

    return null;
  });

  const setItem = useCallback(
    (itemData: T) => {
      localStorage.setItem(key, JSON.stringify(itemData));
      setStorageData(itemData);
    },
    [key],
  );

  const removeItem = useCallback(() => {
    localStorage.removeItem(key);
    setStorageData(null);
  }, [key]);

  return [storageData, setItem, removeItem];
};
