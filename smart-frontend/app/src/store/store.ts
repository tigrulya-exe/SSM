import { combineReducers } from 'redux';
import { configureStore } from '@reduxjs/toolkit';
import notificationsSlice from './notificationsSlice';

const rootReducer = combineReducers({
  notifications: notificationsSlice,
});

// The store setup is wrapped in `makeStore` to allow reuse
// when setting up tests that need the same store config
export const makeStore = (preloadedState?: Partial<RootState>) => {
  const store = configureStore({
    reducer: rootReducer,
    middleware: (getDefaultMiddleware) => {
      return getDefaultMiddleware();
    },
    preloadedState,
  });
  return store;
};

export const store = makeStore();

// Infer the `RootState` type from the root reducer
export type RootState = ReturnType<typeof rootReducer>;
// Infer the type of `store`
export type AppStore = ReturnType<typeof store.getState>;
// Infer the `AppDispatch` type from the store itself
export type AppDispatch = typeof store.dispatch;
