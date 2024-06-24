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
import { combineReducers } from 'redux';
import { configureStore } from '@reduxjs/toolkit';
import notificationsSlice from './notificationsSlice';
import authSlice from '@store/authSlice';
import actionsSlice from '@store/adh/actions/actionsSlice';
import actionsTableSlice from '@store/adh/actions/actionsTableSlice';
import actionsActionsSlice from '@store/adh/actions/actionsActionsSlice';
import auditEventsSlice from '@store/adh/auditEvents/auditEventsSlice';
import auditEventsTableSlice from '@store/adh/auditEvents/auditEventsTableSlice';
import rulesSlice from '@store/adh/rules/rulesSlice';
import rulesTableSlice from '@store/adh/rules/rulesTableSlice';
import rulesActionsSlice from '@store/adh/rules/rulesActionsSlice';

const rootReducer = combineReducers({
  auth: authSlice,
  notifications: notificationsSlice,
  adh: combineReducers({
    actions: actionsSlice,
    actionsTable: actionsTableSlice,
    actionsActions: actionsActionsSlice,
    auditEvents: auditEventsSlice,
    auditEventsTable: auditEventsTableSlice,
    rules: rulesSlice,
    rulesTable: rulesTableSlice,
    rulesActions: rulesActionsSlice,
  }),
});

// The store setup is wrapped in `makeStore` to allow reuse
// when setting up tests that need the same store config
export const makeStore = (preloadedState?: Partial<RootState>) => {
  const store = configureStore({
    reducer: rootReducer,
    middleware: (getDefaultMiddleware) => {
      return getDefaultMiddleware();
    },
    preloadedState,
  });
  return store;
};

export const store = makeStore();

// Infer the `RootState` type from the root reducer
export type RootState = ReturnType<typeof rootReducer>;
// Infer the type of `store`
export type AppStore = ReturnType<typeof store.getState>;
// Infer the `AppDispatch` type from the store itself
export type AppDispatch = typeof store.dispatch;
