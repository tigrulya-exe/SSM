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
import React, { useState, useEffect } from 'react';
import { getFilteredOptions } from './CommonSelectSearchFilter.utils';
import type { SelectOption } from '@uikit/Select/Select.types';
import SearchInput from '@uikit/SearchInput/SearchInput';

interface CommonSelectSearchFilterProps<T> {
  originalOptions: SelectOption<T>[];
  setOptions: (list: SelectOption<T>[]) => void;
  searchPlaceholder?: string;
  className?: string;
  onSearch?: (val: string) => void;
}

const CommonSelectSearchFilter = <T,>({
  originalOptions,
  setOptions,
  searchPlaceholder,
  className,
  onSearch,
}: CommonSelectSearchFilterProps<T>) => {
  const [search, setSearch] = useState('');
  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    const searchStr = e.target.value;
    setSearch(searchStr);
    onSearch?.(searchStr);
  };

  useEffect(() => {
    setOptions(getFilteredOptions(originalOptions, search));
  }, [originalOptions, search, setOptions]);

  return <SearchInput className={className} placeholder={searchPlaceholder} value={search} onChange={handleSearch} />;
};

export default CommonSelectSearchFilter;
