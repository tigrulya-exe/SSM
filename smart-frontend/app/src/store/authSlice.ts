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
import { createSlice } from '@reduxjs/toolkit';
import type { RequestError } from '@api';
import { AuthApi } from '@api';
import { createAsyncThunk } from './redux';
import { getErrorMessage } from '@utils/responseUtils';
import { showError } from './notificationsSlice';
import type { AuthState } from '@models/auth';

type LoginActionPayload = {
  username: string;
  password: string;
};

type UserState = {
  username: string;
  needCheckSession: boolean;
  hasError: boolean;
  authState: AuthState;
  message: string;
};

const login = createAsyncThunk('auth/login', async (arg: LoginActionPayload, thunkAPI) => {
  try {
    await AuthApi.login(arg.username, arg.password);
    return AuthApi.checkSession();
  } catch (error) {
    thunkAPI.dispatch(showError({ message: getErrorMessage(error as RequestError) }));
    return thunkAPI.rejectWithValue(error);
  }
});

const logout = createAsyncThunk('auth/logout', async (_, thunkAPI) => {
  try {
    return AuthApi.logout();
  } catch (error) {
    return thunkAPI.rejectWithValue(error);
  }
});

const checkSession = createAsyncThunk('auth/checkSession', async (_, thunkAPI) => {
  try {
    return AuthApi.checkSession();
  } catch (error) {
    return thunkAPI.rejectWithValue(error);
  }
});

const createInitialState = (): UserState => ({
  username: '',
  needCheckSession: true,
  hasError: false,
  authState: 'NotAuth',
  message: '',
});

const authSlice = createSlice({
  name: 'auth',
  initialState: createInitialState(),
  reducers: {
    clearError(state) {
      state.hasError = false;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(login.pending, (state) => {
      state.message = '';
      state.hasError = false;
      state.needCheckSession = false;
      state.authState = 'Checking';
    });
    builder.addCase(login.fulfilled, (state, action) => {
      // TODO: remove fallback string. In really api should response user info
      state.username = action.payload.name ?? 'testUser';
      state.message = '';
      state.hasError = false;
      state.needCheckSession = false;
      state.authState = 'Authed';
    });
    builder.addCase(login.rejected, (_, action) => {
      const error = action.payload as RequestError;
      return {
        ...createInitialState(),
        message: getErrorMessage(error),
        hasError: true,
        needCheckSession: false,
        authState: 'NotAuth',
      };
    });

    builder.addCase(logout.fulfilled, () => {
      return {
        ...createInitialState(),
        hasError: false,
        needCheckSession: false,
        authState: 'NotAuth',
      };
    });
    builder.addCase(logout.rejected, () => createInitialState());

    builder.addCase(checkSession.pending, (state) => {
      state.hasError = false;
      state.authState = 'Checking';
    });
    builder.addCase(checkSession.fulfilled, (state, action) => {
      // TODO: remove fallback string. In really api should response user info
      state.username = action.payload.name ?? 'testUser';
      state.hasError = false;
      state.needCheckSession = false;
      state.authState = 'Authed';
    });
    builder.addCase(checkSession.rejected, (state) => {
      state.hasError = false;
      state.needCheckSession = false;
      state.authState = 'NotAuth';
    });
  },
});

export const { clearError } = authSlice.actions;
export { login, logout, checkSession };
export default authSlice.reducer;
