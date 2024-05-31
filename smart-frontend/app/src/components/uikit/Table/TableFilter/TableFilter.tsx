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
import React, { useState } from 'react';
import Popover from '@uikit/Popover/Popover';
import cn from 'classnames';
import s from './TableFilter.module.scss';
import type { TableFilterRenderer } from '../Table.types';
import IconButton from '@uikit/IconButton/IconButton';

interface TableFilterProps {
  filterRenderer: TableFilterRenderer;
  hasSetFilter: boolean;
  thRef: React.RefObject<HTMLElement>;
}

const filterPanelOffset = {
  crossAxis: 0,
  mainAxis: 8,
};

const TableFilter: React.FC<TableFilterProps> = ({ filterRenderer, hasSetFilter, thRef }) => {
  const [isFilterOpen, setIsFilterOpen] = useState(false);

  const toggleFilterOpen = () => {
    setIsFilterOpen((prev) => !prev);
  };

  const buttonClasses = cn(s.tableFilter__button, {
    'is-active': isFilterOpen,
    'has-filter': hasSetFilter,
  });

  return (
    <>
      <IconButton
        variant="primary"
        icon="table-filter"
        size={20}
        onClick={toggleFilterOpen}
        className={buttonClasses}
      />
      <Popover
        triggerRef={thRef}
        isOpen={isFilterOpen}
        onOpenChange={setIsFilterOpen}
        placement="bottom-end"
        offset={filterPanelOffset}
      >
        <div>{filterRenderer(toggleFilterOpen, isFilterOpen)}</div>
      </Popover>
    </>
  );
};

export default TableFilter;
