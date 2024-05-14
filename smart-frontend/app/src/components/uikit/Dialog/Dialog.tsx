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
import Modal from '@uikit/Modal/Modal';
import type { ModalOptions } from '@uikit/Modal/Modal.types';
import IconButton from '@uikit/IconButton/IconButton';
import Text from '@uikit/Text/Text';
import type { DialogDefaultControlsProps } from '@uikit/Dialog/DialogDefaultControls';
import DialogDefaultControls from '@uikit/Dialog/DialogDefaultControls';
import Panel from '@uikit/Panel/Panel';
import s from './Dialog.module.scss';
import cn from 'classnames';

export interface DialogProps extends ModalOptions, DialogDefaultControlsProps {
  children: React.ReactNode;
  title?: React.ReactNode;
  dialogControls?: React.ReactNode;
  isDialogControlsOnTop?: boolean;
  width?: string;
  height?: string;
  maxWidth?: string;
  minWidth?: string;
  className?: string;
  dataTest?: string;
}

const Dialog: React.FC<DialogProps> = ({
  isOpen,
  onOpenChange,
  isDismissDisabled,
  children,
  title,
  dialogControls,
  isDialogControlsOnTop,
  cancelButtonLabel,
  actionButtonLabel,
  isActionDisabled,
  onAction,
  onCancel,
  width = '584px',
  height = 'auto',
  maxWidth = '100%',
  minWidth,
  className,
  dataTest = 'dialog-container',
}) => {
  const handleClose = () => {
    onOpenChange(false);
    onCancel?.();
  };

  const handleOpenChange = (isOpen: boolean) => {
    // we can't open Dialog from Dialog, we can close Dialog only
    if (!isOpen) {
      handleClose();
    }
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
    <Modal
      isOpen={isOpen}
      onOpenChange={handleOpenChange}
      className={cn(s.dialog, className)}
      isDismissDisabled={isDismissDisabled}
      style={{ width, height, maxWidth, minWidth }}
      dataTest={dataTest}
    >
      <IconButton
        icon="close"
        size={12}
        className={s.dialog__close}
        onClick={handleClose}
        title="Close"
        tabIndex={-1}
      />
      {title && (
        <Text variant="h2" className={s.dialog__title}>
          {title}
        </Text>
      )}
      {isDialogControlsOnTop && dialogControlsComponent && (
        <Panel className={s.dialog__controlsOnTop} variant="primary">
          {dialogControlsComponent}
        </Panel>
      )}
      <div className={s.dialog__body}>{children}</div>
      {!isDialogControlsOnTop && dialogControlsComponent}
    </Modal>
  );
};
export default Dialog;
