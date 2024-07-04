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
import type { AdhFileInfo } from '@models/adh';

interface AdhHottestFilesSliceState {
  hottestFiles: AdhFileInfo[];
  totalCount: number;
  loadState: LoadState;
}

const loadHottestFiles = createAsyncThunk('adh/hottestFiles/loadHottestFiles', async (_, thunkAPI) => {
  const {
    adh: {
      hottestFilesTable: { filter, paginationParams, sortParams },
    },
  } = thunkAPI.getState();

  try {
    const collection = await AdhFilesApi.getHottestFiles(filter, sortParams, paginationParams);

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

const getHottestFiles = createAsyncThunk('adh/hottestFiles/getHottestFiles', async (_, thunkAPI) => {
  thunkAPI.dispatch(setLoadState(LoadState.Loading));
  const startDate = new Date();

  await thunkAPI.dispatch(loadHottestFiles());

  executeWithMinDelay({
    startDate,
    delay: defaultSpinnerDelay,
    callback: () => {
      thunkAPI.dispatch(setLoadState(LoadState.Loaded));
    },
  });
});

const createInitialState = (): AdhHottestFilesSliceState => ({
  hottestFiles: [],
  totalCount: 0,
  loadState: LoadState.NotLoaded,
});

const hottestFilesSlice = createSlice({
  name: 'adh/hottestFiles',
  initialState: createInitialState(),
  reducers: {
    cleanupHottestFiles() {
      return createInitialState();
    },
    setLoadState(state, action) {
      state.loadState = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(loadHottestFiles.fulfilled, (state, action) => {
      state.hottestFiles = action.payload.items;
      state.totalCount = action.payload.total;
    });
    builder.addCase(loadHottestFiles.rejected, (state) => {
      state.hottestFiles = [];
      state.totalCount = 0;
    });
  },
});

const { cleanupHottestFiles, setLoadState } = hottestFilesSlice.actions;
export { cleanupHottestFiles, getHottestFiles };
export default hottestFilesSlice.reducer;
