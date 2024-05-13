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
