import type { RequestError } from '@api';

export interface ResponseErrorData {
  message: string;
}

export const getErrorMessage = (requestError: RequestError) => {
  const data = (requestError.response?.data ?? {}) as ResponseErrorData;

  return data.message ?? requestError.message ?? 'Something wrong';
};
