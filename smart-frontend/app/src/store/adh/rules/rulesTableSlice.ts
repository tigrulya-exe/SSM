import type { TableState } from '@models/table';
import { createTableSlice } from '@store/redux';
import type { AdhRuleFilter } from '@models/adh';

type AdhTablesTableState = TableState<AdhRuleFilter>;

const createInitialState = (): AdhTablesTableState => ({
  filter: {
    textRepresentationLike: undefined,
    submissionTime: undefined,
    ruleStates: undefined,
    lastActivationTime: undefined,
  },
  paginationParams: {
    perPage: 10,
    pageNumber: 0,
  },
  requestFrequency: 0,
  sortParams: {
    sortBy: 'id',
    sortDirection: 'asc',
  },
});

const rulesTableSlice = createTableSlice({
  name: 'adh/rulesTable',
  createInitialState,
  reducers: {},
  extraReducers: () => {},
});

const {
  //
  setPaginationParams: setRulesPaginationParams,
  cleanupTable: cleanupRulesTable,
  setSortParams: setRulesSortParams,
  setFilter: setRulesFilter,
  resetFilter: resetRulesFilter,
} = rulesTableSlice.actions;

export {
  //
  setRulesPaginationParams,
  cleanupRulesTable,
  setRulesSortParams,
  setRulesFilter,
  resetRulesFilter,
};
export default rulesTableSlice.reducer;
