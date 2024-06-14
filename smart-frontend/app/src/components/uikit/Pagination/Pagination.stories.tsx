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
import Pagination from '@uikit/Pagination/Pagination';
import type { Meta, StoryObj } from '@storybook/react';
import type { PaginationProps } from '@uikit/Pagination/Pagination.types';
import { defaultPerPagesList } from '@constants';
import type { PaginationParams } from '@models/table';

type Story = StoryObj<typeof Pagination>;

export default {
  title: 'uikit/Pagination',
  component: Pagination,
  argTypes: {
    totalItems: {
      defaultValue: 500,
    },
    pageData: {
      defaultValue: { perPage: 10, pageNumber: 4 } as PaginationParams,
    },
    perPageItems: {
      defaultValue: defaultPerPagesList,
    },
    onChangeData: {
      description: 'Function with PaginationData as incoming params',
    },
    isNextBtn: {
      defaultValue: null,
    },
    hidePerPage: {
      defaultValue: false,
    },
  },
} as Meta<typeof Pagination>;

export const PaginationStory: Story = {
  args: {
    pageData: { perPage: 10, pageNumber: 4 },
    totalItems: 145,
  },

  render: (args) => <PaginationExample {...args} />,
};

const PaginationExample = ({ totalItems, pageData, perPageItems, isNextBtn, hidePerPage }: PaginationProps) => {
  const [curPageData, setCurPageData] = useState(pageData);

  const handleOnChange = ({ perPage, pageNumber }: PaginationParams) => {
    setCurPageData({ perPage, pageNumber });
  };

  return (
    <Pagination
      onChangeData={handleOnChange}
      pageData={curPageData}
      totalItems={totalItems}
      perPageItems={perPageItems}
      isNextBtn={isNextBtn}
      hidePerPage={hidePerPage}
    />
  );
};
