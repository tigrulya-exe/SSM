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
import Button from '@uikit/Button/Button';
import React, { useState } from 'react';
import type { FooterDialogProps } from './FooterDialog';
import FooterDialog from './FooterDialog';

type Story = StoryObj<typeof FooterDialog>;
export default {
  title: 'uikit/FooterDialog',
  component: FooterDialog,
  argTypes: {
    onOpenChange: {
      table: {
        disable: true,
      },
    },
  },
} as Meta<typeof FooterDialog>;

const FooterDialogExample: React.FC<FooterDialogProps> = (props) => {
  const [isShown, setIsShown] = useState(false);

  const handleAction = () => {
    props.onAction?.();
    setIsShown(false);
  };

  const handleOpen = () => {
    setIsShown(true);
  };

  return (
    <>
      <Button onClick={handleOpen}>Click to open first footer dialog</Button>
      <div style={{ width: '300px', height: '100px', marginLeft: '100px' }}>
        <FooterDialog {...props} isOpen={isShown} onOpenChange={setIsShown} onAction={handleAction}>
          <div>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam commodo dui vel turpis mollis dignissim.
            Aliquam semper risus sollicitudin, consectetur risus aliquam, fringilla neque. Sed cursus elit eu sem
            bibendum euismod sit amet in erat. Sed id congue libero. Maecenas in commodo nisl, et eleifend lacus. Ut
            convallis eros eget justo sollicitudin pulvinar. Sed eu tellus quis erat auctor tincidunt sit amet eu augue.
            In fermentum egestas mauris vitae porttitor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            facilisi. Sed odio nunc, feugiat vel finibus dapibus, molestie a ipsum. Aenean scelerisque eget ipsum eget
            luctus.
          </div>
        </FooterDialog>
      </div>
    </>
  );
};
export const Dialog: Story = {
  args: {
    title: 'Lorem ipsum',
  },
  render: (args) => {
    return <FooterDialogExample {...args} />;
  },
};
