export const allowIconsNames = [
  //
  'chevron',
  'eye',
  'eye-closed',
  'logout',
  'search',
  'user',
] as const;

export type IconsNames = (typeof allowIconsNames)[number];
