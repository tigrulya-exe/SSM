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
import Button from './Button';
import { fn } from '@storybook/test';

const meta = {
  title: 'uikit/Button',
  component: Button,
  parameters: {
    // Optional parameter to center the component in the Canvas. More info: https://storybook.js.org/docs/configure/story-layout
    layout: 'centered',
  },
  argTypes: {
    disabled: {
      description: 'Disabled',
      control: { type: 'boolean' },
    },
    type: {
      table: {
        disable: true,
      },
    },
    size: {
      description: 'Size',
      options: ['medium', 'small'],
      control: { type: 'radio' },
    },
    variant: {
      description: 'Variant',
      options: ['primary', 'secondary'],
      control: { type: 'radio' },
    },
  },
} as Meta<typeof Button>;

export default meta;

type Story = StoryObj<typeof meta>;

export const ButtonEasy: Story = {
  args: {
    children: 'Create connection',
    variant: 'primary',
    size: 'medium',
    hasError: false,
    onClick: fn(),
  },
};

export const ButtonWithIcon: Story = {
  args: {
    children: 'Some button label',
    variant: 'primary',
    size: 'medium',
    hasError: false,
    onClick: fn(),
  },
};
