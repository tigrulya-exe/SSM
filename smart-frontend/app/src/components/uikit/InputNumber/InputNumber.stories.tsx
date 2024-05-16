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
import InputNumberComponent from './InputNumber';
import type { Meta, StoryObj } from '@storybook/react';

type Story = StoryObj<typeof InputNumberComponent>;
export default {
  title: 'uikit/Input',
  component: InputNumberComponent,
  argTypes: {
    disabled: {
      description: 'Disabled',
      control: { type: 'boolean' },
    },
    variant: {
      table: {
        disable: true,
      },
    },
    placeholder: {
      control: { type: 'text' },
    },
    min: {
      description: 'Disabled',
      control: { type: 'number' },
    },
    max: {
      description: 'Disabled',
      control: { type: 'number' },
    },
  },
} as Meta<typeof InputNumberComponent>;

export const InputNumber: Story = {
  args: {
    placeholder: 'Paste number only',
  },
};
