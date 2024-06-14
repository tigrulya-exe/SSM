import { LoadState } from '@models/loadState';
import { createSlice } from '@reduxjs/toolkit';
import { createAsyncThunk } from '@store/redux';
import { showError } from '@store/notificationsSlice';
import { getErrorMessage } from '@utils/responseUtils';
import type { RequestError } from '@api';
import { AdhRulesApi } from '@api';
import { executeWithMinDelay } from '@utils/requestUtils';
import { defaultSpinnerDelay } from '@constants';
import type { Rule as AdhRule } from '@models/adh_gen/rule';

interface AdhRulesSliceState {
  rules: AdhRule[];
  totalCount: number;
  loadState: LoadState;
}

const loadRules = createAsyncThunk('adh/rules/loadRules', async (_, thunkAPI) => {
  const {
    adh: {
      rulesTable: { filter, paginationParams, sortParams },
    },
  } = thunkAPI.getState();

  try {
    const collection = await AdhRulesApi.getRules(filter, sortParams, paginationParams);

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

const getRules = createAsyncThunk('adh/rules/getRules', async (_, thunkAPI) => {
  thunkAPI.dispatch(setRulesLoadState(LoadState.Loading));
  const startDate = new Date();

  await thunkAPI.dispatch(loadRules());

  executeWithMinDelay({
    startDate,
    delay: defaultSpinnerDelay,
    callback: () => {
      thunkAPI.dispatch(setRulesLoadState(LoadState.Loaded));
    },
  });
});

const refreshRules = createAsyncThunk('adh/rules/refreshRules', async (_, thunkAPI) => {
  thunkAPI.dispatch(loadRules());
});

const createInitialState = (): AdhRulesSliceState => ({
  rules: [],
  totalCount: 0,
  loadState: LoadState.NotLoaded,
});

const rulesSlice = createSlice({
  name: 'adh/rules',
  initialState: createInitialState(),
  reducers: {
    cleanupRules() {
      return createInitialState();
    },
    setLoadState(state, action) {
      state.loadState = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(loadRules.fulfilled, (state, action) => {
      state.rules = action.payload.items;
      state.totalCount = action.payload.total;
    });
    builder.addCase(loadRules.rejected, (state) => {
      state.rules = [];
      state.totalCount = 0;
    });
  },
});

const { cleanupRules, setLoadState: setRulesLoadState } = rulesSlice.actions;
export { cleanupRules, setRulesLoadState, getRules, refreshRules };
export default rulesSlice.reducer;
