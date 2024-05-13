export const allowIconsNames = [
  //
  'chevron',
  'close',
  'check',
  'eye',
  'eye-closed',
  'logout',
  'search',
  'status-error',
  'status-info',
  'status-ok',
  'status-warning',
  'user',
] as const;

export type IconsNames = (typeof allowIconsNames)[number];
