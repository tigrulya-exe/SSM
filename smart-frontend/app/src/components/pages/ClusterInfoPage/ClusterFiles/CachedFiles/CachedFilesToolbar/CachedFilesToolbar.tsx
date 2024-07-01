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
import React from 'react';
import { useDispatch, useStore } from '@hooks';
import type { PaginationParams } from '@models/table';
import { Button, FlexGroup } from '@uikit';
import Pagination from '@uikit/Pagination/Pagination';
import { setPaginationParams, resetFilter } from '@store/adh/cachedFiles/cachedFilesTableSlice';
import s from './CachedFilesToolbar.module.scss';

const CachedFilesToolbar: React.FC = () => {
  const dispatch = useDispatch();
  const totalCount = useStore(({ adh }) => adh.cachedFiles.totalCount);
  const paginationParams = useStore(({ adh }) => adh.cachedFilesTable.paginationParams);

  const handlePaginationChange = (params: PaginationParams) => {
    dispatch(setPaginationParams(params));
  };

  const handleResetClick = () => {
    dispatch(resetFilter());
  };

  return (
    <FlexGroup gap="20px" className={s.cachedFilesToolbar}>
      <Button onClick={handleResetClick} variant="secondary">
        Reset filter
      </Button>
      <Pagination totalItems={totalCount} pageData={paginationParams} onChangeData={handlePaginationChange} />
    </FlexGroup>
  );
};

export default CachedFilesToolbar;
