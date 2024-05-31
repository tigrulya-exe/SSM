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
import { SpinnerPanel } from '@uikit/Spinner/Spinner';
import type { Meta, StoryFn } from '@storybook/react';

const SpinnerStyle = {
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
};

export default {
  title: 'uikit/Spinner',
  component: SpinnerPanel,
  argTypes: {
    size: {
      type: 'number',
      name: 'Size',
      defaultValue: 40,
    },
  },
} as Meta<typeof SpinnerPanel>;

const Template: StoryFn<typeof SpinnerPanel> = (args) => {
  return (
    <div style={SpinnerStyle}>
      <SpinnerPanel {...args} />
    </div>
  );
};

export const SpinnerElement = Template.bind({});
