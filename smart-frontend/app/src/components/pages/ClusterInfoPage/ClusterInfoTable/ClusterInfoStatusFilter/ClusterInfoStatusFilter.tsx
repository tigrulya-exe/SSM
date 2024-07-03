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
import { TableSingleSelectFilter } from '@uikit/Table/TableFilter';
import { getOptionsFromEnum } from '@uikit/Select/Select.utils';
import { type AdhClusterNodesFilter, AdhClusterNodeStatus } from '@models/adh';

const statusesOptions = getOptionsFromEnum(AdhClusterNodeStatus);

interface ClusterInfoStatusFilterProps {
  closeFilter: () => void;
}

const ClusterInfoStatusFilter: React.FC<ClusterInfoStatusFilterProps> = ({ closeFilter }) => {
  return (
    <TableSingleSelectFilter<AdhClusterNodesFilter, AdhClusterNodeStatus>
      filterName="status"
      closeFilter={closeFilter}
      options={statusesOptions}
    />
  );
};

export default ClusterInfoStatusFilter;
