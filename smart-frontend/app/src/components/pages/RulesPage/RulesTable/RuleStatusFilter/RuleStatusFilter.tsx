import React from 'react';
import { TableSingleSelectFilter } from '@uikit/Table/TableFilter';
import { getOptionsFromEnum } from '@uikit/Select/Select.utils';
import { type AdhRuleFilter, AdhRuleState } from '@models/adh';

const statesOptions = getOptionsFromEnum(AdhRuleState);

interface RuleStatusFilterProps {
  closeFilter: () => void;
}

const RuleStatusFilter: React.FC<RuleStatusFilterProps> = ({ closeFilter }) => {
  return (
    <TableSingleSelectFilter<AdhRuleFilter, AdhRuleState>
      filterName="ruleStates"
      closeFilter={closeFilter}
      options={statesOptions}
    />
  );
};

export default RuleStatusFilter;
