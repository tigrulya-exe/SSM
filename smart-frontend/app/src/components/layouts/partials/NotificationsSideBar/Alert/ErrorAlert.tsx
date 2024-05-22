import React from 'react';
import type { ErrorNotification } from '@models/notification';
import Alert from './Alert';
import type { AlertOptions } from './Alert.types';

import s from './Alert.module.scss';

const ErrorAlert: React.FC<ErrorNotification & AlertOptions> = ({ model: { message }, isDisabledClose, onClose }) => {
  return (
    <Alert className={s.alert_error} onClose={onClose} isDisabledClose={isDisabledClose}>
      {message}
    </Alert>
  );
};

export default ErrorAlert;
