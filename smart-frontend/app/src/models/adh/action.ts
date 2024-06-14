import type { DateRange, SerializedDate } from '@models/dateRange';

export const AdhActionSource = {
  Rule: 'RULE',
  User: 'USER',
} as const;
export type AdhActionSource = (typeof AdhActionSource)[keyof typeof AdhActionSource];

export const AdhActionState = {
  Running: 'RUNNING',
  Successful: 'SUCCESSFUL',
  Failed: 'FAILED',
} as const;
export type AdhActionState = (typeof AdhActionState)[keyof typeof AdhActionState];

export interface AdhAction {
  id: number;
  cmdletId: number;
  textRepresentation: string;
  execHost?: string | null;
  submissionTime: number;
  completionTime?: number | null;
  state: AdhActionState;
  source: AdhActionSource;
  log?: string;
}

export interface AdhActionsFilter {
  textRepresentationLike?: string;
  submissionTime?: DateRange<SerializedDate>;
  hosts?: string[];
  states?: AdhActionState[];
  sources?: AdhActionSource[];
  completionTime?: DateRange<SerializedDate>;
}
