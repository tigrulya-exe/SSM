export enum ExecutorType {
  Local = 'LOCAL',
  RemoteSsm = 'REMOTE_SSM',
  Agent = 'AGENT',
}

export interface ClusterNode {
  id: string;
  host: string;
  port: number;
  executorType: ExecutorType;
  registrationTime: number;
  executorsCount: number;
  cmdletsExecuted: number;
}
