import type { PayloadAction } from '@reduxjs/toolkit';
import { createSlice, nanoid } from '@reduxjs/toolkit';
import type { ErrorNotification, InfoNotification, Notification, SuccessNotification } from '@models/notification';
import { NotificationVariant } from '@models/notification';

interface AlertOptions {
  id?: string;
  ttl?: number;
  message: string;
  isDisabledClose?: boolean;
}

type NotificationsState = {
  notifications: Notification[];
};

const appendNotification = (list: Notification[], newNotification: Notification) => {
  const lastNotification = list.at(-1);

  // not show duplicate message
  if (lastNotification?.model.message !== newNotification.model.message) {
    return [...list, newNotification];
  }

  return list;
};

const createInitialState = (): NotificationsState => ({
  notifications: [],
});

const notificationsSlice = createSlice({
  name: 'notifications',
  initialState: createInitialState(),
  reducers: {
    showError(state, action: PayloadAction<AlertOptions>) {
      const { ttl, id, message, isDisabledClose } = action.payload;

      const notification: ErrorNotification = {
        id: id ?? nanoid(),
        variant: NotificationVariant.Error,
        model: {
          message,
        },
        ttl: ttl ?? 5000,
        isDisabledClose,
      };
      state.notifications = appendNotification(state.notifications, notification);
    },
    showInfo(state, action: PayloadAction<AlertOptions>) {
      const { ttl, id, message, isDisabledClose } = action.payload;

      const notification: InfoNotification = {
        id: id ?? nanoid(),
        variant: NotificationVariant.Info,
        model: {
          message,
        },
        ttl: ttl ?? 5000,
        isDisabledClose,
      };
      state.notifications = appendNotification(state.notifications, notification);
    },
    showSuccess(state, action: PayloadAction<AlertOptions>) {
      const { ttl, id, message, isDisabledClose } = action.payload;

      const notification: SuccessNotification = {
        id: id ?? nanoid(),
        variant: NotificationVariant.Success,
        model: {
          message,
        },
        ttl: ttl ?? 5000,
        isDisabledClose,
      };
      state.notifications = appendNotification(state.notifications, notification);
    },
    closeNotification(state, action: PayloadAction<string>) {
      state.notifications = state.notifications.filter((n) => n.id !== action.payload);
    },
    cleanupNotifications() {
      return createInitialState();
    },
  },
});

export const { showInfo, showError, closeNotification, cleanupNotifications } = notificationsSlice.actions;
export default notificationsSlice.reducer;
