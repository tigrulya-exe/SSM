import type { DateRange, SerializedDate } from '@models/dateRange';

export enum AdhAuditObjectType {
  Rule = 'RULE',
  Cmdlet = 'CMDLET',
}

export enum AdhAuditOperation {
  Create = 'CREATE',
  Delete = 'DELETE',
  Start = 'START',
  Stop = 'STOP',
}

export enum AdhAuditEventResult {
  Success = 'SUCCESS',
  Failure = 'FAILURE',
}

export interface AdhAuditEvent {
  id: number;
  username: string;
  timestamp: number;
  objectType: AdhAuditObjectType;
  objectId: number;
  operation: AdhAuditOperation;
  result: AdhAuditEventResult;
}

export interface AdhAuditEventsFilter {
  usernameLike?: string;
  eventTime?: DateRange<SerializedDate>;
  objectTypes?: AdhAuditObjectType[];
  objectIds?: number[];
  operations?: AdhAuditOperation[];
  results?: AdhAuditEventResult[];
}
