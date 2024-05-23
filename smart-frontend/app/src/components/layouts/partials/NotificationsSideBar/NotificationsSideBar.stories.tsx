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
import React, { useState } from 'react';
import type { Meta, StoryObj } from '@storybook/react';
import Alert from '@layouts/partials/NotificationsSideBar/Alert/Alert';
import { store } from '@store';
import { Provider } from 'react-redux';
import { Button } from '@uikit';
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
