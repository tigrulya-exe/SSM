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
import type { Meta, StoryObj } from '@storybook/react';
import Tooltip from './Tooltip';
import Button from '@uikit/Button/Button';

type Story = StoryObj<typeof Tooltip>;

export default {
  title: 'uikit/Tooltip',
  component: Tooltip,
} as Meta<typeof Tooltip>;

export const EasyTooltip: Story = {
  args: {
    label: 'Some tooltip text',
    offset: 8,
    placement: 'bottom',
  },
  render: (args) => {
    return (
      <Tooltip {...args}>
        <Button>Some button</Button>
      </Tooltip>
    );
  },
};

export const DisabledButtonTooltip: Story = {
  args: {
    label: 'This button disabled, but tooltip is showing',
    offset: 8,
    placement: 'right',
  },
  render: (args) => {
    return (
      <Tooltip {...args}>
        <Button disabled>Some button</Button>
      </Tooltip>
    );
  },
};
