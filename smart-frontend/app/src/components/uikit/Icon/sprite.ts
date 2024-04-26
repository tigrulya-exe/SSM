export const allowIconsNames = [
  //
  'chevron',
  'logout',
  'search',
  'user',
] as const;

export type IconsNames = (typeof allowIconsNames)[number];
