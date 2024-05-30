import type { Nullable } from '@models/utils';

type GetCallback<T, R> = (data: T) => R;

export const isValidData = <T>(data: T | undefined | null): data is T =>
  !(Number.isNaN(data) || typeof data === 'undefined' || data === null);

export const orElseGet = <T, R>(
  data: Nullable<T>,
  callback?: GetCallback<T, R> | null,
  placeholder: R = '-' as R,
): R => {
  if (!isValidData(data) || data === '') return placeholder as R;

  return (callback ? callback(data) : data) as R;
};

export const isPromiseFulfilled = <T>(p: PromiseSettledResult<T>): p is PromiseFulfilledResult<T> =>
  p.status === 'fulfilled';
export const isPromiseRejected = <T>(p: PromiseSettledResult<T>): p is PromiseRejectedResult => p.status === 'rejected';

export const fulfilledFilter = <T>(list: PromiseSettledResult<T>[]) =>
  list.filter(isPromiseFulfilled).map(({ value }) => value);

export const rejectedFilter = <T>(list: PromiseSettledResult<T>[]) =>
  list.filter(isPromiseRejected).map(({ reason }) => reason);

export const isNumber = (value?: number | string) => {
  return value !== null && value !== '' && !Number.isNaN(Number(value));
};
