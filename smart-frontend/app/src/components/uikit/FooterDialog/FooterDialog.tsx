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
import type { ModalOptions } from '@uikit/Modal/Modal.types';
import Text from '@uikit/Text/Text';
import type { DialogDefaultControlsProps } from '@uikit/FooterDialog/FooterDialogDefaultControls';
import DialogDefaultControls from '@uikit/FooterDialog/FooterDialogDefaultControls';
import Panel from '@uikit/Panel/Panel';
import s from './FooterDialog.module.scss';
import cn from 'classnames';
import { FloatingPortal } from '@floating-ui/react';

export interface FooterDialogProps extends ModalOptions, DialogDefaultControlsProps {
  isShown: boolean;
  children: React.ReactNode;
  title?: React.ReactNode;
  dialogControls?: React.ReactNode;
  className?: string;
}

const FooterDialog: React.FC<FooterDialogProps> = ({
  isShown,
  onOpenChange,
  children,
  title,
  dialogControls,
  cancelButtonLabel,
  actionButtonLabel,
  isActionDisabled,
  onAction,
  onCancel,
  className,
}) => {
  const handleClose = () => {
    onOpenChange(false);
    onCancel?.();
  };

  const dialogControlsComponent = dialogControls ?? (
    <DialogDefaultControls
      cancelButtonLabel={cancelButtonLabel}
      actionButtonLabel={actionButtonLabel}
      isActionDisabled={isActionDisabled}
      onAction={onAction}
      onCancel={handleClose}
    />
  );

  return (
    <FloatingPortal>
      <div className={cn(s.dialog, className, { [s.dialog__shown]: isShown })}>
        <div className={s.dialog__header}>
          {title && (
            <Text variant="h2" className={s.dialog__title}>
              {title}
            </Text>
          )}
          {dialogControlsComponent && (
            <Panel className={s.dialog__controls} variant="primary">
              {dialogControlsComponent}
            </Panel>
          )}
        </div>
        <div className={s.dialog__body}>{children}</div>
      </div>
    </FloatingPortal>
  );
};
export default FooterDialog;
