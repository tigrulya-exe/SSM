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
import type { PaginateCollection } from '@models/collection';
import { httpClient } from '@api/httpClient';
import type { AdhClusterNodesFilter, AdhClusterNode } from '@models/adh';
import type { PaginationParams, SortParams } from '@models/table';
import { prepareNamedDateRange, prepareQueryParams } from '@utils/requestUtils';
import qs from 'qs';

export class AdhClusterInfoApi {
  public static async getNodes(
    { registrationTime, ...filter }: AdhClusterNodesFilter,
    sortParams?: SortParams,
    paginationParams?: PaginationParams,
  ): Promise<PaginateCollection<AdhClusterNode>> {
    const queryParams = prepareQueryParams(
      {
        ...filter,
        ...prepareNamedDateRange(registrationTime, 'registrationTime'),
      },
      sortParams,
      paginationParams,
    );

    const query = qs.stringify(queryParams, { arrayFormat: 'repeat' });

    const response = await httpClient.get<PaginateCollection<AdhClusterNode>>(`/api/v2/cluster/nodes?${query}`);

    return response.data;
  }
}
