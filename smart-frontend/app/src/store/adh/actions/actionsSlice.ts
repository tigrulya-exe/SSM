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
import { LoadState } from '@models/loadState';
import { createSlice } from '@reduxjs/toolkit';
import { createAsyncThunk } from '@store/redux';
import { showError } from '@store/notificationsSlice';
import { getErrorMessage } from '@utils/responseUtils';
import type { RequestError } from '@api';
import { AdhActionsApi } from '@api';
import { executeWithMinDelay } from '@utils/requestUtils';
import { defaultSpinnerDelay } from '@constants';
import type { AdhAction } from '@models/adh';

interface AdhActionsSliceState {
  actions: AdhAction[];
  totalCount: number;
  loadState: LoadState;
}

const loadActions = createAsyncThunk('adh/actions/loadActions', async (_, thunkAPI) => {
  const {
    adh: {
      actionsTable: { filter, paginationParams, sortParams },
    },
  } = thunkAPI.getState();

  try {
    const collection = await AdhActionsApi.getActions(filter, sortParams, paginationParams);

    return collection;
  } catch (error) {
    thunkAPI.dispatch(
      showError({
        message: getErrorMessage(error as RequestError),
      }),
    );
    return thunkAPI.rejectWithValue(error);
  }
});

const getActions = createAsyncThunk('adh/actions/getActions', async (_, thunkAPI) => {
  thunkAPI.dispatch(setActionsLoadState(LoadState.Loading));
  const startDate = new Date();

  await thunkAPI.dispatch(loadActions());

  executeWithMinDelay({
    startDate,
    delay: defaultSpinnerDelay,
    callback: () => {
      thunkAPI.dispatch(setActionsLoadState(LoadState.Loaded));
    },
  });
});

const refreshActions = createAsyncThunk('adh/actions/refreshActions', async (_, thunkAPI) => {
  thunkAPI.dispatch(loadActions());
});

const createInitialState = (): AdhActionsSliceState => ({
  actions: [],
  totalCount: 0,
  loadState: LoadState.NotLoaded,
});

const actionsSlice = createSlice({
  name: 'adh/actions',
  initialState: createInitialState(),
  reducers: {
    cleanupActions() {
      return createInitialState();
    },
    setLoadState(state, action) {
      state.loadState = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(loadActions.fulfilled, (state, action) => {
      state.actions = action.payload.items;
      state.totalCount = action.payload.total;
    });
    builder.addCase(loadActions.rejected, (state) => {
      state.actions = [];
      state.totalCount = 0;
    });
  },
});

const { cleanupActions, setLoadState: setActionsLoadState } = actionsSlice.actions;
export { cleanupActions, setActionsLoadState, getActions, refreshActions };
export default actionsSlice.reducer;
