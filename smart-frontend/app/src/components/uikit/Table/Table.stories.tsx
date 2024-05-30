/* eslint-disable spellcheck/spell-checker */
import Table from './Table';
import TableCell from './TableCell/TableCell';
import IconButton from '@uikit/IconButton/IconButton';
import type { Meta, StoryObj } from '@storybook/react';
import TableRow from './TableRow/TableRow';
import React, { useEffect, useState } from 'react';
import type { SortParams } from '@models/table';
import TableSingleSelectFilter from '@uikit/Table/TableFilter/TableSingleSelectFilter/TableSingleSelectFilter';
import TableSearchFilter from '@uikit/Table/TableFilter/TableSearchFilter/TableSearchFilter';
import TableMultiSelectFilter from '@uikit/Table/TableFilter/TableMultiSelectFilter/TableMultiSelectFilter';
import type { TableColumnSchema } from '@uikit/Table/Table.types';
import type { DateRange, SerializedDate } from '@models/dateRange';
import { TableDateRangePickerFilter } from '@uikit/Table/TableFilter';

type Story = StoryObj<typeof Table>;

export default {
  title: 'uikit/TableV3/Table',
  component: Table,
  argTypes: {},
} as Meta<typeof Table>;

enum States {
  Installed = 'installed',
  State1 = 'state1',
  State2 = 'state2',
  State3 = 'state3',
}

enum Products {
  ADBX = 'adbx',
  ADBY = 'adby',
  ADBZ = 'adbz',
}

interface TableFilter {
  id?: string;
  state?: States;
  product?: Products[];
  started?: DateRange<SerializedDate>;
}

const stateOptions = [
  {
    label: 'Installed',
    value: States.Installed,
  },
  {
    label: 'State1',
    value: States.State1,
  },
  {
    label: 'State2',
    value: States.State2,
  },
  {
    label: 'State3',
    value: States.State3,
  },
];

const productsOptions = [
  {
    label: 'ADBX',
    value: Products.ADBX,
  },
  {
    label: 'ADBY',
    value: Products.ADBY,
  },
  {
    label: 'ADBZ',
    value: Products.ADBZ,
  },
];

const columns = [
  {
    name: 'id',
    label: 'ID',
    isSortable: true,
    filterRenderer: () => {
      return <TableSearchFilter<TableFilter> filterName="id" />;
    },
  },
  {
    name: 'name',
    label: 'Name',
    isSortable: true,
  },
  {
    name: 'state',
    label: 'State',
    filterRenderer: (closeCallback) => {
      return (
        <TableSingleSelectFilter<TableFilter, States>
          filterName="state"
          options={stateOptions}
          closeFilter={closeCallback}
        />
      );
    },
  },
  {
    name: 'product',
    label: 'Product',
    filterRenderer: () => {
      return <TableMultiSelectFilter<TableFilter, Products> filterName="product" options={productsOptions} />;
    },
  },
  {
    name: 'version',
    label: 'Version',
  },
  {
    name: 'started',
    label: 'Started',
    filterRenderer: (closeFilter) => {
      return <TableDateRangePickerFilter<TableFilter> filterName="started" closeFilter={closeFilter} />;
    },
  },
  {
    name: 'actions',
    label: 'Actions',
  },
] as TableColumnSchema[];

const data = [
  {
    id: 1,
    name: 'Quiet Oka1',
    state: 'installed',
    product: 'ADB-z',
    version: '6.22.1',
    started: 170212,
  },
  {
    id: 2,
    name: 'Quiet Oka2',
    state: 'installed',
    product: 'ADB-y',
    version: '6.22.1',
    started: 170212,
  },
  {
    id: 3,
    name: 'Quiet Oka3',
    state: 'installed',
    product: 'ADB-x',
    version: '6.22.1',
    started: 170212,
  },
];

export const TableExample: Story = {
  args: {},
  render: (args) => {
    const [sortParams, setSort] = useState<SortParams>({ sortBy: 'id', sortDirection: 'asc' });

    const [filter, setFilter] = useState({
      id: undefined,
    });

    useEffect(() => {
      console.info('filter after update', filter);
    }, [filter]);

    return (
      <Table
        {...args}
        columns={columns}
        sortParams={sortParams}
        onSorting={setSort}
        filter={filter}
        onFiltering={setFilter}
      >
        {data.map((entity) => (
          <TableRow key={entity.id}>
            <TableCell>{entity.id}</TableCell>
            <TableCell>{entity.name}</TableCell>
            <TableCell>{entity.state}</TableCell>
            <TableCell>{entity.product}</TableCell>
            <TableCell>{entity.version}</TableCell>
            <TableCell>{entity.started}</TableCell>
            <TableCell>
              <IconButton icon="chevron" size={28} />
            </TableCell>
          </TableRow>
        ))}
      </Table>
    );
  },
};
