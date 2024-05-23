/**
 * date time in seconds, use for send request to Api, and save values to local storage
 */
export type SerializedDate = number;

/**
 * By default, `from` and `to` instances of Date  (new Date())
 */
export type StaticDateRange<T = Date> = {
  from: T;
  to: T;
};

export type DynamicDateRange =
  | 'now-1h'
  | 'now-2h'
  | 'now-4h'
  | 'now-8h'
  | 'now-12h'
  | 'now-24h'
  | 'now-2d'
  | 'now-5d'
  | 'now-7d'
  | 'now-14d'
  | 'now-1M';

export type DateRange<T = Date> = StaticDateRange<T> | DynamicDateRange;

export const isDynamicDateRange = <T = Date>(range?: DateRange<T>): range is DynamicDateRange => {
  return typeof range === 'string';
};

export const isStaticDateRange = <T = Date>(range?: DateRange<T>): range is StaticDateRange<T> => {
  return typeof range === 'object';
};
