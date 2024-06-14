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

    const query = qs.stringify(queryParams);

    const response = await httpClient.get<PaginateCollection<AdhRule>>(`/api/v2/rules?${query}`);

    return response.data;
  }
}
