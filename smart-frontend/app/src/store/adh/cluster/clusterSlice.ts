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
import { AdhClusterInfoApi, type RequestError } from '@api';
import { executeWithMinDelay } from '@utils/requestUtils';
import { defaultSpinnerDelay } from '@constants';
import { AdhClusterNodeStatus, type AdhClusterNode } from '@models/adh';

interface AdhClustersSliceState {
  nodes: AdhClusterNode[];
  totalCount: number;
  loadState: LoadState;
  liveCount: number;
}

const loadClusterNodes = createAsyncThunk('adh/cluster/loadClusterNodes', async (_, thunkAPI) => {
  const {
    adh: {
      clusterTable: { filter, paginationParams, sortParams },
    },
  } = thunkAPI.getState();

  try {
    const collection = await AdhClusterInfoApi.getNodes(filter, sortParams, paginationParams);

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

const getClusterNodes = createAsyncThunk('adh/cluster/getClusterNodes', async (_, thunkAPI) => {
  thunkAPI.dispatch(setClustersLoadState(LoadState.Loading));
  const startDate = new Date();

  await thunkAPI.dispatch(loadClusterNodes());

  executeWithMinDelay({
    startDate,
    delay: defaultSpinnerDelay,
    callback: () => {
      thunkAPI.dispatch(setClustersLoadState(LoadState.Loaded));
    },
  });
});

const refreshClusterNodes = createAsyncThunk('adh/cluster/refreshClusterNodes', async (_, thunkAPI) => {
  thunkAPI.dispatch(loadClusterNodes());
});

const getClusterMetaInfo = createAsyncThunk('adh/cluster/getClusterMetaInfo', async (_, thunkAPI) => {
  try {
    const activeCollection = await AdhClusterInfoApi.getNodes({ clusterStates: [AdhClusterNodeStatus.Active] });

    return activeCollection;
  } catch (error) {
    thunkAPI.dispatch(
      showError({
        message: getErrorMessage(error as RequestError),
      }),
    );
    return thunkAPI.rejectWithValue(error);
  }
});

const createInitialState = (): AdhClustersSliceState => ({
  nodes: [],
  totalCount: 0,
  loadState: LoadState.NotLoaded,
  liveCount: 0,
});

const clustersSlice = createSlice({
  name: 'adh/cluster',
  initialState: createInitialState(),
  reducers: {
    cleanupClusterNodes() {
      return createInitialState();
    },
    setLoadState(state, action) {
      state.loadState = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(loadClusterNodes.fulfilled, (state, action) => {
      state.nodes = action.payload.items;
      state.totalCount = action.payload.total;
    });
    builder.addCase(loadClusterNodes.rejected, (state) => {
      state.nodes = [];
      state.totalCount = 0;
    });
    builder.addCase(getClusterMetaInfo.fulfilled, (state, action) => {
      state.liveCount = action.payload.total;
    });
    builder.addCase(getClusterMetaInfo.rejected, (state) => {
      state.liveCount = 0;
    });
  },
});

const { cleanupClusterNodes, setLoadState: setClustersLoadState } = clustersSlice.actions;
export { cleanupClusterNodes, setClustersLoadState, getClusterNodes, refreshClusterNodes, getClusterMetaInfo };
export default clustersSlice.reducer;
