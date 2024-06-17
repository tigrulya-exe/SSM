import { createCrudSlice } from '@store/redux/createCrudSlice';
import type { AdhRule } from '@models/adh';
import type { ModalState } from '@models/modal';
import { createAsyncThunk } from '@store/redux';
// eslint-disable-next-line import/no-cycle
import { getRules } from './rulesSlice';
import { showError, showSuccess } from '@store/notificationsSlice';
import { getErrorMessage } from '@utils/responseUtils';
import type { RequestError } from '@api';
import { AdhRulesApi } from '@api';
import type { PayloadAction } from '@reduxjs/toolkit';

const createRule = createAsyncThunk('adh/rulesActions/createRule', async (text: string, thunkAPI) => {
  thunkAPI.dispatch(setIsActionInProgress(true));
  thunkAPI.dispatch(closeCreateRuleDialog());
  try {
    await AdhRulesApi.createRule(text);
    thunkAPI.dispatch(showSuccess({ message: 'New rule was created and applied successfully' }));
  } catch (error) {
    thunkAPI.dispatch(showError({ message: getErrorMessage(error as RequestError) }));
    return thunkAPI.rejectWithValue(error);
  } finally {
    thunkAPI.dispatch(setIsActionInProgress(false));
  }
});

const deleteRule = createAsyncThunk('adh/rulesActions/deleteRule', async (ruleId: number, thunkAPI) => {
  thunkAPI.dispatch(setIsActionInProgress(true));
  thunkAPI.dispatch(closeDeleteRuleDialog());
  try {
    await AdhRulesApi.deleteRule(ruleId);
    thunkAPI.dispatch(showSuccess({ message: `Rule #${ruleId} was deleted` }));
  } catch (error) {
    thunkAPI.dispatch(showError({ message: getErrorMessage(error as RequestError) }));
    return thunkAPI.rejectWithValue(error);
  } finally {
    thunkAPI.dispatch(setIsActionInProgress(false));
  }
});

const startRule = createAsyncThunk('adh/rulesActions/startRule', async (ruleId: number, thunkAPI) => {
  thunkAPI.dispatch(closeStartRuleDialog());
  try {
    await AdhRulesApi.startRule(ruleId);
    thunkAPI.dispatch(showSuccess({ message: `Rule #${ruleId} was started successfully` }));
  } catch (error) {
    thunkAPI.dispatch(showError({ message: getErrorMessage(error as RequestError) }));
    return thunkAPI.rejectWithValue(error);
  }
});

const stopRule = createAsyncThunk('adh/rulesActions/stopRule', async (ruleId: number, thunkAPI) => {
  thunkAPI.dispatch(closeStopRuleDialog());
  try {
    await AdhRulesApi.stopRule(ruleId);
    thunkAPI.dispatch(showSuccess({ message: `Rule #${ruleId} was stopped successfully` }));
  } catch (error) {
    thunkAPI.dispatch(showError({ message: getErrorMessage(error as RequestError) }));
    return thunkAPI.rejectWithValue(error);
  }
});

const withUpdate = (name: string, dialogAction: ReturnType<typeof createAsyncThunk>) => {
  return createAsyncThunk(name, async (payload: unknown, thunkAPI) => {
    await thunkAPI.dispatch(dialogAction(payload)).unwrap();
    await thunkAPI.dispatch(getRules());
  });
};

const createRuleWithUpdate = withUpdate(
  'adh/rulesActions/createRuleWithUpdate',
  createRule as ReturnType<typeof createAsyncThunk>,
);

const deleteRuleWithUpdate = withUpdate(
  'adh/rulesActions/deleteRuleWithUpdate',
  deleteRule as ReturnType<typeof createAsyncThunk>,
);

interface AdhRulesActions extends ModalState<AdhRule, 'rule'> {
  startDialog: {
    rule: AdhRule | null;
  };
  stopDialog: {
    rule: AdhRule | null;
  };
}

const createInitialState = (): AdhRulesActions => ({
  createDialog: {
    isOpen: false,
  },
  deleteDialog: {
    rule: null,
  },
  updateDialog: {
    rule: null,
  },
  startDialog: {
    rule: null,
  },
  stopDialog: {
    rule: null,
  },
  isActionInProgress: false,
});

const rulesActionsSlice = createCrudSlice({
  name: 'adh/rulesActions',
  entityName: 'rule',
  createInitialState,
  reducers: {
    openStartRuleDialog(state, action: PayloadAction<AdhRule>) {
      state.startDialog.rule = action.payload;
    },
    openStopRuleDialog(state, action: PayloadAction<AdhRule>) {
      state.stopDialog.rule = action.payload;
    },
    closeStartRuleDialog(state) {
      state.startDialog.rule = null;
    },
    closeStopRuleDialog(state) {
      state.stopDialog.rule = null;
    },
  },
  extraReducers: () => {},
});

const {
  openCreateDialog: openCreateRuleDialog,
  openUpdateDialog: openUpdateRuleDialog,
  openDeleteDialog: openDeleteRuleDialog,
  closeCreateDialog: closeCreateRuleDialog,
  closeUpdateDialog: closeUpdateRuleDialog,
  closeDeleteDialog: closeDeleteRuleDialog,
  setIsActionInProgress,
  openStartRuleDialog,
  openStopRuleDialog,
  closeStartRuleDialog,
  closeStopRuleDialog,
} = rulesActionsSlice.actions;

export {
  openCreateRuleDialog,
  openUpdateRuleDialog,
  openDeleteRuleDialog,
  closeCreateRuleDialog,
  closeUpdateRuleDialog,
  closeDeleteRuleDialog,
  openStartRuleDialog,
  openStopRuleDialog,
  closeStartRuleDialog,
  closeStopRuleDialog,
  createRuleWithUpdate,
  deleteRuleWithUpdate,
  startRule,
  stopRule,
};

export default rulesActionsSlice.reducer;
