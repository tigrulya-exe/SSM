import type {
  SliceCaseReducers,
  ValidateSliceCaseReducers,
  PayloadAction,
  ActionReducerMapBuilder,
  Draft,
} from '@reduxjs/toolkit';
import { createSlice } from '@reduxjs/toolkit';

import type { PaginationParams, SortParams, TableState } from '@models/table';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
type ExtractFilter<S> = S extends TableState<infer F, infer E> ? F : never;
// eslint-disable-next-line @typescript-eslint/no-unused-vars
type ExtractEntity<S> = S extends TableState<infer F, infer E> ? E : never;

type ExtractSortParams<S> = SortParams<ExtractEntity<S>>;

interface CreateTableSliceOptions<
  S extends TableState<ExtractFilter<S>, ExtractEntity<S>>,
  CR extends SliceCaseReducers<S>,
  Name extends string = string,
> {
  name: Name;
  createInitialState: () => S;
  reducers: ValidateSliceCaseReducers<S, CR>;
  extraReducers?: (builder: ActionReducerMapBuilder<S>) => void;
}

export function createTableSlice<
  S extends TableState<ExtractFilter<S>, ExtractEntity<S>>,
  CR extends SliceCaseReducers<S>,
>(options: CreateTableSliceOptions<S, CR>) {
  const { name, createInitialState, reducers, extraReducers } = options;

  return createSlice({
    name,
    initialState: createInitialState(),
    reducers: {
      ...reducers,
      setFilter(state, action: PayloadAction<Partial<ExtractFilter<S>>>) {
        state.filter = {
          ...state.filter,
          ...action.payload,
        };
        state.paginationParams.pageNumber = 0;
      },
      resetFilter(state) {
        const initialData = createInitialState();
        state.filter = initialData.filter as Draft<ExtractFilter<S>>;
      },
      setPaginationParams(state, action: PayloadAction<PaginationParams>) {
        state.paginationParams = action.payload;
      },
      setSortParams(state, action: PayloadAction<ExtractSortParams<S>>) {
        state.sortParams = action.payload as Draft<ExtractSortParams<S>>;
      },
      resetSortParams(state) {
        state.sortParams = createInitialState().sortParams as Draft<ExtractSortParams<S>>;
      },
      setRequestFrequency(state, action: PayloadAction<number>) {
        state.requestFrequency = action.payload;
      },
      // method for combine reset table's setting
      resetSettings(state) {
        const initialData = createInitialState();
        state.filter = initialData.filter as Draft<ExtractFilter<S>>;
        state.sortParams = initialData.sortParams as Draft<ExtractSortParams<S>>;
        state.paginationParams = initialData.paginationParams as Draft<PaginationParams>;
      },
      cleanupTable() {
        return createInitialState();
      },
    },
    extraReducers,
  });
}
