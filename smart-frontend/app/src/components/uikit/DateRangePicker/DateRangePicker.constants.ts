import type { RangePreset } from './DateRangePicker.types';

export const defaultRangesPreset: RangePreset[] = [
  { id: 'now-1h', description: 'last 1 hour' },
  { id: 'now-2h', description: 'last 2 hours' },
  { id: 'now-4h', description: 'last 4 hours' },
  { id: 'now-8h', description: 'last 8 hours' },
  { id: 'now-12h', description: 'last 12 hours' },
  { id: 'now-24h', description: 'last 24 hours' },
  { id: 'now-2d', description: 'last 2 days' },
  { id: 'now-5d', description: 'last 5 days' },
  { id: 'now-7d', description: 'last 7 days' },
  { id: 'now-14d', description: 'last 2 weeks' },
  { id: 'now-1M', description: 'last month' },
];
