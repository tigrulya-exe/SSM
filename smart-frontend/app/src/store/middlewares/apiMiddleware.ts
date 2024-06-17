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
import type { UnknownAction, Middleware } from 'redux';
import { isRejectedWithValue } from '@reduxjs/toolkit';
import { logout } from '../authSlice';
import type { RootState } from '../store';
import type { RequestError } from '@api';

export const apiMiddleware: Middleware<
  // eslint-disable-next-line @typescript-eslint/ban-types
  {},
  RootState
> = (storeApi) => (next) => (action) => {
  if (isRejectedWithValue(action)) {
    const response = (action.payload as RequestError)?.response;
    if (response?.status === 401 || response?.status === 410) {
      // not reasons call logout after mistake login
      if (action.type !== 'auth/login/rejected') {
        storeApi.dispatch(logout() as unknown as UnknownAction);
      }
    }
  }
  next(action);
};
