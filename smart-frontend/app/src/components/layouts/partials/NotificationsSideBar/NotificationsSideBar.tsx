import React, { useEffect } from 'react';
import { useDispatch, useStore } from '@hooks';
import type { Notification } from '@models/notification';
import { NotificationVariant } from '@models/notification';
import ErrorAlert from './Alert/ErrorAlert';
import InfoAlert from './Alert/InfoAlert';
import SuccessAlert from './Alert/SuccessAlert';
import { closeNotification } from '@store/notificationsSlice';
import s from './NotificationsSideBar.module.scss';

const NotificationItem: React.FC<Notification> = (props) => {
  const { ttl, variant, id } = props;
  const dispatch = useDispatch();
  const removeNotification = () => {
    dispatch(closeNotification(id));
  };

  useEffect(() => {
    let timer: ReturnType<typeof setTimeout>;
    if (ttl) {
      timer = setTimeout(() => {
        removeNotification();
      }, ttl);
    }

    return () => clearTimeout(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (variant === NotificationVariant.Error) {
    return <ErrorAlert {...props} onClose={removeNotification} />;
  }
  if (variant === NotificationVariant.Info) {
    return <InfoAlert {...props} onClose={removeNotification} />;
  }
  if (variant === NotificationVariant.Success) {
    return <SuccessAlert {...props} onClose={removeNotification} />;
  }

  return null;
};

const NotificationsSideBar: React.FC = () => {
  const notifications = useStore((s) => s.notifications.notifications);

  return (
    <div className={s.notificationsSideBar} data-test="notification-container">
      {notifications.map((item) => {
        return <NotificationItem key={item.id} {...item} />;
      })}
    </div>
  );
};
export default NotificationsSideBar;
