import React from 'react';
import RulesResetFilter from '../RulesResetFilter/RulesResetFilter';
import FlexGroup from '@uikit/FlexGroup/FlexGroup';
import Pagination from '@uikit/Pagination/Pagination';
import { useDispatch, useStore } from '@hooks';
import type { PaginationParams } from '@models/table';
import { setRulesPaginationParams } from '@store/adh/rules/rulesTableSlice';
import s from './RulesToolbar.module.scss';

const RulesToolbar: React.FC = () => {
  const dispatch = useDispatch();
  const totalCount = useStore(({ adh }) => adh.rules.totalCount);
  const paginationParams = useStore(({ adh }) => adh.rulesTable.paginationParams);

  const handlePaginationChange = (params: PaginationParams) => {
    dispatch(setRulesPaginationParams(params));
  };

  return (
    <FlexGroup gap="20px" className={s.rulesToolbar}>
      <RulesResetFilter />
      <Pagination totalItems={totalCount} pageData={paginationParams} onChangeData={handlePaginationChange} />
    </FlexGroup>
  );
};

export default RulesToolbar;
