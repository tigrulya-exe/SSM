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
/* eslint-disable spellcheck/spell-checker */
import { useState } from 'react';
import Table from '@uikit/Table/Table';
import TableCell from '@uikit/Table/TableCell/TableCell';
import IconButton from '@uikit/IconButton/IconButton';
import ExpandableRowComponent from './ExpandableRow';
import type { Meta, StoryObj } from '@storybook/react';
import type { TableColumnSchema } from '@uikit/Table/Table.types';

type Story = StoryObj<typeof ExpandableRowComponent>;
export default {
  title: 'uikit/Table/ExpandableRow',
  component: ExpandableRowComponent,
  argTypes: {},
} as Meta<typeof ExpandableRowComponent>;

const columns = [
  {
    name: 'id',
    label: 'ID',
  },
  {
    name: 'name',
    label: 'Name',
  },
  {
    name: 'state',
    label: 'State',
  },
  {
    name: 'product',
    label: 'Product',
  },
  {
    name: 'version',
    label: 'Version',
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
  },
  {
    id: 2,
    name: 'Quiet Oka2',
    state: 'installed',
    product: 'ADB-y',
    version: '6.22.1',
  },
  {
    id: 3,
    name: 'Quiet Oka3',
    state: 'installed',
    product: 'ADB-x',
    version: '6.22.1',
  },
];

const longText =
  'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.';

export const ExpandableRow: Story = {
  args: {},
  render: (args) => {
    const [expandableRows, setExpandableRows] = useState<Record<number, boolean>>({
      1: false,
      2: false,
      3: false,
    });

    const handleExpandClick = (id: number) => {
      setExpandableRows({
        ...expandableRows,
        [id]: !expandableRows[id],
      });
    };

    const handleSorting = () => {
      console.info('sorting');
    };

    return (
      <Table {...args} columns={columns} sortParams={{ sortBy: 'id', sortDirection: 'asc' }} onSorting={handleSorting}>
        {data.map((entity) => (
          <ExpandableRowComponent
            key={entity.id}
            isExpanded={expandableRows[entity.id]}
            expandedContent={<div>{longText}</div>}
          >
            <TableCell>{entity.id}</TableCell>
            <TableCell>{entity.name}</TableCell>
            <TableCell>{entity.state}</TableCell>
            <TableCell>{entity.product}</TableCell>
            <TableCell>{entity.version}</TableCell>
            <TableCell>
              <IconButton icon="chevron" size={14} onClick={() => handleExpandClick(entity.id)} />
            </TableCell>
          </ExpandableRowComponent>
        ))}
      </Table>
    );
  },
};
