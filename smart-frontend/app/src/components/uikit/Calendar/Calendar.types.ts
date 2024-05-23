import type { MouseEventHandler } from 'react';

export type CalendarMap = Date[][];
export type MonthSwitchDirections = 'prev' | 'next';

export type ChangeMonthHandler = (direction: MonthSwitchDirections) => MouseEventHandler;
