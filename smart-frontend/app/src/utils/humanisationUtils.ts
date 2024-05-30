import type { SelectOption } from '@uikit/Select/Select.types';

export const generateOptions = <T>(keys: T[], getLabel?: (key: T) => string): SelectOption<T>[] => {
  return keys.map((key) => ({
    label: getLabel ? getLabel(key) : key?.toString() || '',
    value: key,
  }));
};

export const getStatusLabel = (status: string) => {
  const statusLabel = status.trim().replaceAll('_', ' ');
  return statusLabel[0].toUpperCase() + (statusLabel?.slice(1).toLowerCase() ?? '');
};
