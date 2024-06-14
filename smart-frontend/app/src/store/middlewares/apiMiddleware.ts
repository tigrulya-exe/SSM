import type { UnknownAction, Middleware } from 'redux';
import { isRejectedWithValue } from '@reduxjs/toolkit';
import { logout } from '../authSlice';
import type { RootState } from '../store';
import type { RequestError } from '@api';

export const apiMiddleware: Middleware<
  // eslint-disable-next-line @typescript-eslint/ban-types
  {},
  RootState
> = (storeApi) => (next) => (action) => {
  if (isRejectedWithValue(action)) {
    const response = (action.payload as RequestError)?.response;
    if (response?.status === 401 || response?.status === 410) {
      // not reasons call logout after mistake login
      if (action.type !== 'auth/login/rejected') {
        storeApi.dispatch(logout() as unknown as UnknownAction);
      }
    }
  }
  next(action);
};
