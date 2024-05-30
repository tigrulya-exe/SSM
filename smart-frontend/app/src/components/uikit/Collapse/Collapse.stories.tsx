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
import Collapse from './Collapse';
import Button from '@uikit/Button/Button';

const meta = {
  title: 'uikit/Collapse',
  component: Collapse,
  parameters: {
    // Optional parameter to center the component in the Canvas. More info: https://storybook.js.org/docs/configure/story-layout
    layout: 'padded',
  },
} as Meta<typeof Collapse>;

export default meta;

type Story = StoryObj<typeof meta>;

const style = {
  color: '#757b81',
  border: '1px solid #757b81',
  marginTop: '20px',
  padding: '16px',
};

const CollapseEasy: React.FC = () => {
  const [isExpanded, setExpanded] = useState(true);
  return (
    <>
      <Button
        onClick={() => {
          setExpanded((prevExpanded) => !prevExpanded);
        }}
      >
        {isExpanded ? 'Close' : 'Open'}
      </Button>
      <Collapse isExpanded={isExpanded}>
        <div style={style}>
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit, <br />
          sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
          <br />
          Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi <br />
          ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit
          <br />
          in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
          <br />
          Excepteur sint occaecat cupidatat non proident, <br />
          sunt in culpa qui officia deserunt mollit anim id est laborum."
          <br />
          <br />
          <br />
        </div>
      </Collapse>
    </>
  );
};

export const CollapseExample: Story = {
  render: (args) => {
    return <CollapseEasy {...args} />;
  },
};
