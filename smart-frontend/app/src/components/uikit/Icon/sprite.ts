export const allowIconsNames = [
  //
  'chevron',
  'close',
  'eye',
  'eye-closed',
  'logout',
  'search',
  'user',
] as const;

export type IconsNames = (typeof allowIconsNames)[number];
