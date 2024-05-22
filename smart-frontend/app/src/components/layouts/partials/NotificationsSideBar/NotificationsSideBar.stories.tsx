import React, { useState } from 'react';
import type { Meta, StoryObj } from '@storybook/react';
import Alert from '@layouts/partials/NotificationsSideBar/Alert/Alert';
import { store } from '@store';
import { Provider } from 'react-redux';
import Button from '@uikit/Button/Button';
import { useDispatch } from '@hooks';
import { showError, showInfo } from '@store/notificationsSlice';
import NotificationsSideBar from '@layouts/partials/NotificationsSideBar/NotificationsSideBar';

type Story = StoryObj<typeof Alert>;

export default {
  title: 'uikit/Notifications',
  component: Alert,
  decorators: [
    (Story) => {
      return (
        <Provider store={store}>
          <Story />
          <NotificationsSideBar />
        </Provider>
      );
    },
  ],
  argTypes: {
    children: {
      table: {
        disable: true,
      },
    },
  },
} as Meta<typeof Alert>;

export const Notification: Story = {
  render: () => {
    return <NotificationSidebarExample />;
  },
};

const NotificationSidebarExample: React.FC = () => {
  const dispatch = useDispatch();

  const [counter, setCounter] = useState(0);

  return (
    <div style={{ display: 'flex', gap: 30 }}>
      <Button
        variant="secondary"
        onClick={() => {
          dispatch(
            showInfo({
              message: 'Some Info text ' + counter,
            }),
          );
          setCounter((prev) => prev + 1);
        }}
      >
        Show info notification
      </Button>
      <Button
        hasError
        onClick={() => {
          dispatch(
            showError({
              message: 'Some errors text ' + counter,
            }),
          );
          setCounter((prev) => prev + 1);
        }}
      >
        Show error notification
      </Button>
      <Button
        onClick={() => {
          dispatch(
            showError({
              message: 'Some Success text ' + counter,
            }),
          );
          setCounter((prev) => prev + 1);
        }}
      >
        Show success notification
      </Button>
    </div>
  );
};
