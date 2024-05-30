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
