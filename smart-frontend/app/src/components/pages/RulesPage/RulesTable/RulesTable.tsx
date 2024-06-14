import React from 'react';
import Table from '@uikit/Table/Table';
import { rulesColumns } from '@pages/RulesPage/RulesTable/RulesTable.schema';
import { useDispatch, useStore } from '@hooks';
import { isShowSpinner } from '@uikit/Table/Table.utils';
import TableRow from '@uikit/Table/TableRow/TableRow';
import TableCellsRenderer from '@uikit/Table/TableCell/TableCellsRenderer';
import { setRulesFilter, setRulesSortParams } from '@store/adh/rules/rulesTableSlice';
import type { SortParams } from '@models/table';
import type { AdhRuleFilter } from '@models/adh';

const RulesTable: React.FC = () => {
  const dispatch = useDispatch();
  const rules = useStore(({ adh }) => adh.rules.rules);
  const isLoading = useStore(({ adh }) => isShowSpinner(adh.rules.loadState));

  const filter = useStore(({ adh }) => adh.rulesTable.filter);
  const sortParams = useStore(({ adh }) => adh.rulesTable.sortParams);

  const handleFiltering = (filter: Partial<AdhRuleFilter>) => {
    dispatch(setRulesFilter(filter));
  };
  const handleSorting = (sortParams: SortParams) => {
    dispatch(setRulesSortParams(sortParams));
  };

  return (
    <Table
      isLoading={isLoading}
      columns={rulesColumns}
      filter={filter}
      onFiltering={handleFiltering}
      sortParams={sortParams}
      onSorting={handleSorting}
    >
      {rules.map((rule) => (
        <TableRow key={rule.id}>
          <TableCellsRenderer model={rule} />
        </TableRow>
      ))}
    </Table>
  );
};

export default RulesTable;
