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
/* eslint-disable spellcheck/spell-checker */
import React, { useRef, useState } from 'react';
import type { Meta, StoryObj } from '@storybook/react';
import type { PopoverProps } from './Popover';
import Popover from './Popover';
import Button from '@uikit/Button/Button';
import Input from '@uikit/Input/Input';
import PopoverPanelDefault from '@uikit/Popover/PopoverPanelDefault/PopoverPanelDefault';

type Story = StoryObj<typeof Popover>;
export default {
  title: 'uikit/Popover',
  component: Popover,
  argTypes: {
    children: {
      table: {
        disable: true,
      },
    },
    isOpen: {
      table: {
        disable: true,
      },
    },
    onOpenChange: {
      table: {
        disable: true,
      },
    },
    triggerRef: {
      table: {
        disable: true,
      },
    },
  },
} as Meta<typeof Popover>;

const EasyPopoverExample: React.FC<PopoverProps> = ({ dependencyWidth, placement, offset }) => {
  const [isOpen, setIsOpen] = useState(false);
  const localRef = useRef(null);

  const handleClick = () => {
    setIsOpen((prev) => !prev);
  };

  return (
    <div style={{ minHeight: 'calc(100vh - 2rem)', display: 'flex' }}>
      <Button ref={localRef} onClick={handleClick} style={{ margin: 'auto' }}>
        Click for show popover
      </Button>
      <Popover
        isOpen={isOpen}
        onOpenChange={setIsOpen}
        triggerRef={localRef}
        dependencyWidth={dependencyWidth}
        placement={placement}
        offset={offset}
      >
        <PopoverPanelDefault style={{ fontSize: '30px', minHeight: '100px', padding: '20px' }}>
          Show Popup content
        </PopoverPanelDefault>
      </Popover>
    </div>
  );
};
export const EasyPopover: Story = {
  args: {
    placement: 'bottom',
    offset: 8,
    dependencyWidth: 'min-parent',
  },
  render: (args) => {
    return <EasyPopoverExample {...args} />;
  },
};

const PopoverInPopoverExample: React.FC = () => {
  const [isPrimaryOpen, setIsPrimaryOpen] = useState(false);
  const primaryRef = useRef(null);
  const handlePrimaryClick = () => {
    setIsPrimaryOpen((prev) => !prev);
  };

  const [isSecondaryOpen, setIsSecondaryOpen] = useState(false);
  const secondaryRef = useRef(null);

  const handleSecondaryClick = () => {
    setIsSecondaryOpen((prev) => !prev);
  };

  return (
    <div style={{ display: 'flex' }}>
      <Button ref={primaryRef} onClick={handlePrimaryClick} style={{ margin: 'auto' }}>
        Click for show Primary Popover
      </Button>
      <Popover isOpen={isPrimaryOpen} onOpenChange={setIsPrimaryOpen} triggerRef={primaryRef}>
        <PopoverPanelDefault style={{ fontSize: '30px', minHeight: '200px', padding: '20px' }}>
          <div style={{ display: 'flex' }}>
            <Button ref={secondaryRef} onClick={handleSecondaryClick} style={{ margin: 'auto' }}>
              Click for show Secondary Popover
            </Button>
            <Popover isOpen={isSecondaryOpen} onOpenChange={setIsSecondaryOpen} triggerRef={secondaryRef}>
              <PopoverPanelDefault style={{ fontSize: '20px', minHeight: '100px', padding: '10px' }}>
                Show Popup content
                <Input />
              </PopoverPanelDefault>
            </Popover>
          </div>
        </PopoverPanelDefault>
      </Popover>
    </div>
  );
};

export const PopoverInPopover: Story = {
  render: () => {
    return <PopoverInPopoverExample />;
  },
};
