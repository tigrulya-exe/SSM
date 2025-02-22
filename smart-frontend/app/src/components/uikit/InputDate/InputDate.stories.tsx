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
import InputDate from './InputDate';
import type { Meta, StoryObj } from '@storybook/react';
import { useEffect, useState } from 'react';

type Story = StoryObj<typeof InputDate>;

export default {
  title: 'uikit/InputDate',
  component: InputDate,
  argTypes: {
    placeholder: {
      defaultValue: 'Select date...',
      description: 'Placeholder',
      control: { type: 'text' },
    },
    value: {
      defaultValue: new Date(),
      description: 'value',
      control: { type: 'date' },
    },
  },
} as Meta<typeof InputDate>;

export const InputDateStory: Story = {
  args: {
    value: new Date(),
  },
  render: ({ value, ...args }) => {
    const [localValue, setLocalValue] = useState<Date | undefined>(value);

    useEffect(() => {
      setLocalValue(value);
    }, [value]);

    return (
      <div style={{ width: 600, display: 'flex', alignItems: 'center' }}>
        <InputDate {...args} value={localValue} onChange={setLocalValue} />
      </div>
    );
  },
};

export const EmptyInputDateStory: Story = {
  args: {
    value: undefined,
  },
  render: ({ value, ...args }) => {
    const [localValue, setLocalValue] = useState<Date | undefined>(value);

    useEffect(() => {
      setLocalValue(value);
    }, [value]);

    return (
      <div style={{ width: 600, display: 'flex', alignItems: 'center' }}>
        <InputDate {...args} value={localValue} onChange={setLocalValue} />
      </div>
    );
  },
};
