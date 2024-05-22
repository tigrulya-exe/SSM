import React from 'react';
import cn from 'classnames';
import { Button } from '@uikit';

import s from './Alert.module.scss';
import type { AlertOptions } from './Alert.types';

interface AlertProps extends AlertOptions {
  isDisabledClose?: boolean;
}

const Alert: React.FC<AlertProps> = ({ children, className, onClose, isDisabledClose = false }) => {
  return (
    <div className={cn(className, s.alert)}>
      <div>{children}</div>
      <Button className={s.alert__button} size="small" variant="secondary" onClick={onClose} disabled={isDisabledClose}>
        OK
      </Button>
    </div>
  );
};

export default Alert;
