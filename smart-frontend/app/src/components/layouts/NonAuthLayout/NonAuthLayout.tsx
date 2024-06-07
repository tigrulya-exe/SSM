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
import React, { useEffect } from 'react';
import s from './NonAuthLayout.module.scss';
import HeaderToolbar from '@layouts/partials/HeaderToolbar/HeaderToolbar';
import NotificationsSideBar from '@layouts/partials/NotificationsSideBar/NotificationsSideBar';
import { useDispatch } from '@hooks';
import { cleanupNotifications } from '@store/notificationsSlice';
import PageContent from '@layouts/partials/PageContent/PageContent';
import MainLogo from '@layouts/partials/MainLogo/MainLogo';
import UnionLogo from './images/union.svg?react';

const NonAuthLayout: React.FC<React.PropsWithChildren> = ({ children }) => {
  const dispatch = useDispatch();

  useEffect(() => {
    return () => {
      dispatch(cleanupNotifications());
    };
  }, [dispatch]);

  return (
    <div className={s.nonAuthLayout}>
      <div className={s.nonAuthLayout__leftSide}>
        <div>
          <MainLogo height="40px" />
          <div className={s.nonAuthLayout__description}>
            Arenadata Smart Storage Management
            <br />
            Manage your data effectively?
          </div>
          <UnionLogo className={s.nonAuthLayout__union} />
        </div>
      </div>
      <PageContent className={s.nonAuthLayout__rightSide}>
        <header className={s.nonAuthLayout__header}>
          <HeaderToolbar />
        </header>
        <div className={s.nonAuthLayout__rightContent}>{children}</div>
      </PageContent>
      <NotificationsSideBar />
    </div>
  );
};

export default NonAuthLayout;
