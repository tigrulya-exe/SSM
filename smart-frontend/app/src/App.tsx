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
import { Provider } from 'react-redux';
import { BrowserRouter, Outlet, Route, Routes } from 'react-router-dom';
import { store } from '@store';
import './styles/app.scss';
import MainLayout from '@layouts/MainLayout/MainLayout';
import ClusterInfoPage from '@pages/ClusterInfoPage/ClusterInfoPage';
import RulesPage from '@pages/RulesPage/RulesPage';
import ActionsPage from '@pages/ActionsPage/ActionsPage';
import AuditPage from '@pages/AuditPage/AuditPage';

function App() {
  return (
    <BrowserRouter>
      <Provider store={store}>
        <Routes>
          {/*<Route path="/login" element={<LoginPage />} />*/}
          <Route
            path="/"
            element={
              <MainLayout>
                <Outlet />
              </MainLayout>
            }
          >
            <Route index element={<ClusterInfoPage />} />
            <Route path="/rules" element={<RulesPage />} />
            <Route path="/actions" element={<ActionsPage />} />
            <Route path="/audit" element={<AuditPage />} />
          </Route>
        </Routes>
      </Provider>
    </BrowserRouter>
  );
}

export default App;
