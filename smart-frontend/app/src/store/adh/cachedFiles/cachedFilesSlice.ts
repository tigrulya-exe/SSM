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
import { AdhFilesApi } from '@api';
import { executeWithMinDelay } from '@utils/requestUtils';
import { defaultSpinnerDelay } from '@constants';
import type { AdhCachedFileInfo } from '@models/adh';

interface AdhFilesSliceState {
  cachedFiles: AdhCachedFileInfo[];
  totalCount: number;
  loadState: LoadState;
}

const loadCachedFiles = createAsyncThunk('adh/cachedFiles/loadCachedFiles', async (_, thunkAPI) => {
  const {
    adh: {
      cachedFilesTable: { filter, paginationParams, sortParams },
    },
  } = thunkAPI.getState();

  try {
    const collection = await AdhFilesApi.getCachedFiles(filter, sortParams, paginationParams);

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

const getCachedFiles = createAsyncThunk('adh/cachedFiles/getCachedFiles', async (_, thunkAPI) => {
  thunkAPI.dispatch(setLoadState(LoadState.Loading));
  const startDate = new Date();

  await thunkAPI.dispatch(loadCachedFiles());

  executeWithMinDelay({
    startDate,
    delay: defaultSpinnerDelay,
    callback: () => {
      thunkAPI.dispatch(setLoadState(LoadState.Loaded));
    },
  });
});

const createInitialState = (): AdhFilesSliceState => ({
  cachedFiles: [],
  totalCount: 0,
  loadState: LoadState.NotLoaded,
});

const cachedFilesSlice = createSlice({
  name: 'adh/cachedFiles',
  initialState: createInitialState(),
  reducers: {
    cleanupCachedFiles() {
      return createInitialState();
    },
    setLoadState(state, action) {
      state.loadState = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(loadCachedFiles.fulfilled, (state, action) => {
      state.cachedFiles = action.payload.items;
      state.totalCount = action.payload.total;
    });
    builder.addCase(loadCachedFiles.rejected, (state) => {
      state.cachedFiles = [];
      state.totalCount = 0;
    });
  },
});

const { cleanupCachedFiles, setLoadState } = cachedFilesSlice.actions;
export { cleanupCachedFiles, getCachedFiles };
export default cachedFilesSlice.reducer;
