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
import type { Meta, StoryObj } from '@storybook/react';
import { Select } from '@uikit';

type Story = StoryObj<typeof Select>;
export default {
  title: 'uikit/Select',
  argTypes: {
    isSearchable: {
      control: { type: 'boolean' },
    },
    hasError: {
      control: { type: 'boolean' },
    },
    isDisabled: {
      control: { type: 'boolean' },
    },
    noneLabel: {
      control: { type: 'text' },
    },
  },
  component: Select,
} as Meta<typeof Select>;

const options = [
  {
    value: 123,
    label: 'A 123',
  },
  {
    value: 234,
    label: 'A 234',
  },
  {
    value: 345,
    label: 'A 345',
  },
  {
    value: 456,
    label: 'A 456',
  },
  {
    value: 567,
    label: 'A 567',
  },
  {
    value: 678,
    label: 'A 678',
  },
  {
    value: 789,
    label: 'A 789',
  },
];

type SingleSelectExampleProps = {
  isSearchable?: boolean;
  isDisabled?: boolean;
  hasError?: boolean;
  noneLabel?: string;
};
const SingleSelectExample: React.FC<SingleSelectExampleProps> = ({ isSearchable, isDisabled, hasError, noneLabel }) => {
  const [value, setValue] = useState<number | null>(null);

  return (
    <div style={{ padding: 30, display: 'flex', alignItems: 'center' }}>
      <Select
        value={value}
        onChange={setValue}
        options={options}
        isSearchable={isSearchable}
        hasError={hasError}
        disabled={isDisabled}
        noneLabel={noneLabel}
      />
    </div>
  );
};

export const SingleSelect: Story = {
  args: {
    isSearchable: false,
    hasError: false,
    isDisabled: false,
    noneLabel: undefined,
  },
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  render: ({ isSearchable, isDisabled, hasError, noneLabel }) => {
    return (
      <SingleSelectExample
        isSearchable={isSearchable}
        hasError={hasError}
        isDisabled={isDisabled}
        noneLabel={noneLabel}
      />
    );
  },
};
