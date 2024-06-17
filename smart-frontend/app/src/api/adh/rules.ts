import type { AdhRule, AdhRuleFilter } from '@models/adh';
import type { PaginationParams, SortParams } from '@models/table';
import type { PaginateCollection } from '@models/collection';
import { prepareDateRange, prepareQueryParams } from '@utils/requestUtils';
import { httpClient } from '@api/httpClient';
import qs from 'qs';

export class AdhRulesApi {
  public static async getRules(
    { submissionTime, lastActivationTime, ...filter }: AdhRuleFilter,
    sortParams: SortParams,
    paginationParams: PaginationParams,
  ): Promise<PaginateCollection<AdhRule>> {
    const { from: submissionTimeFrom, to: submissionTimeTo } = prepareDateRange(submissionTime);
    const { from: lastActivationTimeFrom, to: lastActivationTimeTo } = prepareDateRange(lastActivationTime);

    const queryParams = prepareQueryParams(
      {
        ...filter,
        submissionTime: { submissionTimeFrom, submissionTimeTo },
        lastActivationTime: { lastActivationTimeFrom, lastActivationTimeTo },
      },
      sortParams,
      paginationParams,
    );

    const query = qs.stringify(queryParams, { arrayFormat: 'repeat' });

    const response = await httpClient.get<PaginateCollection<AdhRule>>(`/api/v2/rules?${query}`);

    return response.data;
  }

  public static async createRule(text: string) {
    const response = await httpClient.post('/api/v2/rules', {
      rule: text,
    });

    return response.data;
  }

  public static async deleteRule(id: number) {
    const response = await httpClient.delete(`/api/v2/rules/${id}`);

    return response.data;
  }

  public static async startRule(id: number) {
    const response = await httpClient.post(`/api/v2/rules/${id}/start`);

    return response.data;
  }

  public static async stopRule(id: number) {
    const response = await httpClient.post(`/api/v2/rules/${id}/stop`);

    return response.data;
  }
}
