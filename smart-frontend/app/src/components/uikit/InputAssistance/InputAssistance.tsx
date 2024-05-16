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
import React from 'react';
import Checkbox from '@uikit/Checkbox/Checkbox';
import Popover from '@uikit/Popover/Popover';

import s from './InputAssistance.module.scss';
import cn from 'classnames';
import PopoverPanelDefault from '@uikit/Popover/PopoverPanelDefault/PopoverPanelDefault';
import { useLocalStorage } from '@hooks/useLocalStorage';

export interface InputAssistanceProps {
  id: string;
  isOpen: boolean;
  className?: string;
  children: React.ReactNode;
  triggerRef: React.RefObject<HTMLElement>;
  focusRef?: React.RefObject<HTMLElement>;
  onOpenChange: (isOpen: boolean) => void;
}

const InputAssistance = ({
  id,
  className = '',
  children,
  isOpen,
  triggerRef,
  focusRef,
  onOpenChange,
}: InputAssistanceProps) => {
  const [doNotShowExplanations, storeDoNotShowExplanations] = useLocalStorage({
    key: `hide-${id}`,
    initData: false,
  });

  const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    storeDoNotShowExplanations(e.target.checked);
  };

  if (doNotShowExplanations) {
    return null;
  }

  return (
    <Popover
      triggerRef={triggerRef}
      initialFocus={focusRef}
      isOpen={isOpen}
      onOpenChange={onOpenChange}
      placement="bottom-start"
      dependencyWidth="min-parent"
      offset={5}
    >
      <PopoverPanelDefault className={cn(s.assistance, className)}>
        <div className={s.assistanceSection}>{children}</div>
        <div className={cn(s.assistanceSection, s.doNotShow)}>
          <Checkbox
            checked={Boolean(doNotShowExplanations)}
            onChange={handleCheckboxChange}
            label="Don't show this message again"
          />
        </div>
      </PopoverPanelDefault>
    </Popover>
  );
};

export default InputAssistance;
