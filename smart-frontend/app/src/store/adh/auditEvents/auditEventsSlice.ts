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
import { AdhAuditEventsApi } from '@api';
import { executeWithMinDelay } from '@utils/requestUtils';
import { defaultSpinnerDelay } from '@constants';
import type { AdhAuditEvent } from '@models/adh';

interface AdhAuditEventsSliceState {
  auditEvents: AdhAuditEvent[];
  totalCount: number;
  loadState: LoadState;
}

const loadAuditEvents = createAsyncThunk('adh/auditEvents/loadAuditEvents', async (_, thunkAPI) => {
  const {
    adh: {
      auditEventsTable: { filter, paginationParams, sortParams },
    },
  } = thunkAPI.getState();

  try {
    const collection = await AdhAuditEventsApi.getAuditEvents(filter, sortParams, paginationParams);

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

const getAuditEvents = createAsyncThunk('adh/auditEvents/getAuditEvents', async (_, thunkAPI) => {
  thunkAPI.dispatch(setAuditEventsLoadState(LoadState.Loading));
  const startDate = new Date();

  await thunkAPI.dispatch(loadAuditEvents());

  executeWithMinDelay({
    startDate,
    delay: defaultSpinnerDelay,
    callback: () => {
      thunkAPI.dispatch(setAuditEventsLoadState(LoadState.Loaded));
    },
  });
});

const refreshAuditEvents = createAsyncThunk('adh/auditEvents/refreshAuditEvents', async (_, thunkAPI) => {
  thunkAPI.dispatch(loadAuditEvents());
});

const createInitialState = (): AdhAuditEventsSliceState => ({
  auditEvents: [],
  totalCount: 0,
  loadState: LoadState.NotLoaded,
});

const auditEventsSlice = createSlice({
  name: 'adh/auditEvents',
  initialState: createInitialState(),
  reducers: {
    cleanupAuditEvents() {
      return createInitialState();
    },
    setLoadState(state, action) {
      state.loadState = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(loadAuditEvents.fulfilled, (state, action) => {
      state.auditEvents = action.payload.items;
      state.totalCount = action.payload.total;
    });
    builder.addCase(loadAuditEvents.rejected, (state) => {
      state.auditEvents = [];
      state.totalCount = 0;
    });
  },
});

const { cleanupAuditEvents, setLoadState: setAuditEventsLoadState } = auditEventsSlice.actions;
export { cleanupAuditEvents, setAuditEventsLoadState, getAuditEvents, refreshAuditEvents };
export default auditEventsSlice.reducer;
