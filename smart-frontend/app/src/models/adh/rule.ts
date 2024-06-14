import type { DateRange, SerializedDate } from '@models/dateRange';

export interface AdhRule {
  id: number;
  submitTime: number;
  textRepresentation: string;
  state: AdhRuleState;
  activationCount: number;
  cmdletsGenerated: number;
  lastActivationTime?: number | null;
}

export const AdhRuleState = {
  Active: 'ACTIVE',
  Disabled: 'DISABLED',
} as const;

export type AdhRuleState = (typeof AdhRuleState)[keyof typeof AdhRuleState];

export interface AdhRuleFilter {
  textRepresentationLike?: string;
  submissionTime?: DateRange<SerializedDate>;
  ruleStates?: AdhRuleState[];
  lastActivationTime?: DateRange<SerializedDate>;
}
