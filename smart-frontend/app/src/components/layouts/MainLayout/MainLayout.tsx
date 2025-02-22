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
import s from './MainLayout.module.scss';
import NotificationsSideBar from '@layouts/partials/NotificationsSideBar/NotificationsSideBar';
import MainHeader from '@layouts/partials/MainHeader/MainHeader';
import MainLeftSideBar from '@layouts/partials/MainLeftSideBar/MainLeftSideBar';
import PageContent from '@layouts/partials/PageContent/PageContent';

const MainLayout: React.FC<React.PropsWithChildren> = ({ children }) => {
  return (
    <div className={s.mainLayout}>
      <MainLeftSideBar />
      <PageContent className={s.mainLayout__body}>
        <MainHeader />
        {children}
      </PageContent>
      <NotificationsSideBar />
    </div>
  );
};

export default MainLayout;
