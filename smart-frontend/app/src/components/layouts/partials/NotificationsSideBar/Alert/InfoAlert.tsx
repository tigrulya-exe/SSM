import React from 'react';
import Alert from './Alert';
import type { InfoNotification } from '@models/notification';
import type { AlertOptions } from './Alert.types';

import s from './Alert.module.scss';

const InfoAlert: React.FC<InfoNotification & AlertOptions> = ({ model: { message }, isDisabledClose, onClose }) => {
  return (
    <Alert className={s.alert_info} onClose={onClose} isDisabledClose={isDisabledClose}>
      {message}
    </Alert>
  );
};

export default InfoAlert;
