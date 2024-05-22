import type { AsyncThunkPayloadCreator } from '@reduxjs/toolkit';
// eslint-disable-next-line @typescript-eslint/no-restricted-imports
import { createAsyncThunk as createReduxAsyncThunk } from '@reduxjs/toolkit';
import type { AppStore, AppDispatch } from '../store';
export { createTableSlice } from '@store/redux/createTableSlice';

type ThunkApiConfig = { state: AppStore; dispatch: AppDispatch };

export function createAsyncThunk<Returned, ThunkArg = void>(
  typePrefix: string,
  payloadCreator: AsyncThunkPayloadCreator<Returned, ThunkArg, ThunkApiConfig>,
) {
  return createReduxAsyncThunk<Returned, ThunkArg>(typePrefix, payloadCreator);
}
