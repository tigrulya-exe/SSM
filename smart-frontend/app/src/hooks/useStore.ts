import type { TypedUseSelectorHook } from 'react-redux';
// eslint-disable-next-line @typescript-eslint/no-restricted-imports
import { useSelector as useReduxSelector } from 'react-redux';
import type { AppStore } from '@store/store';

// Use instead of `useSelector` from 'react-redux
export const useStore: TypedUseSelectorHook<AppStore> = useReduxSelector;
