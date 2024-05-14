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
import type { StoryFn, Meta } from '@storybook/react';
import PasswordInput from './PasswordInput';

export default {
  title: 'uikit/PasswordInput',
  component: PasswordInput,
  argTypes: {
    size: {
      description: 'Size',
      defaultValue: 'medium',
      options: ['medium', 'small'],
      control: { type: 'radio' },
    },
    variant: {
      description: 'Variant',
      defaultValue: 'primary',
      options: ['primary', 'secondary'],
      control: { type: 'radio' },
    },
  },
} as Meta<typeof PasswordInput>;

const Template: StoryFn<typeof PasswordInput> = (args) => {
  return (
    <div style={{ padding: '40px' }}>
      <PasswordInput {...args} />
    </div>
  );
};

export const PasswordInputElement = Template.bind({
  size: 'medium',
  variant: 'primary',
});
