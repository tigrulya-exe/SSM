import type { TableColumnSchema } from '@uikit/Table/Table.types';
import { SchemaColumnType } from '@uikit/Table/Table.types';
import { TableDateRangePickerFilter, TableMultiSelectFilter, TableSearchFilter } from '@uikit/Table/TableFilter';
import type { AdhRule, AdhRuleFilter } from '@models/adh';
import { AdhRuleState } from '@models/adh';
import RuleStatusCell from './RuleStatusCell/RuleStatusCell';
import RuleActionsCell from './RuleActionsCell/RuleActionsCell';
import { getOptionsFromEnum } from '@uikit/Select/Select.utils';

const ruleStatesOptions = getOptionsFromEnum(AdhRuleState);

export const rulesColumns: TableColumnSchema[] = [
  {
    name: 'id',
    label: 'ID',
    isSortable: true,
  },
  {
    name: 'textRepresentation',
    label: 'Rule Text',
    filterRenderer: () => {
      return <TableSearchFilter<AdhRuleFilter> filterName="textRepresentationLike" placeholder="Search text like" />;
    },
    filterName: 'textRepresentationLike',
    schema: {
      type: SchemaColumnType.BigText,
    },
  },
  {
    name: 'submitTime',
    label: 'Submission Time',
    isSortable: true,
    filterRenderer: (closeFilter) => {
      return <TableDateRangePickerFilter<AdhRuleFilter> filterName="submissionTime" closeFilter={closeFilter} />;
    },
    filterName: 'submissionTime',
    schema: {
      type: SchemaColumnType.DateTime,
    },
  },
  {
    name: 'lastActivationTime',
    label: 'Last Check Time',
    isSortable: true,
    filterRenderer: (closeFilter) => {
      return <TableDateRangePickerFilter<AdhRuleFilter> filterName="lastActivationTime" closeFilter={closeFilter} />;
    },
    filterName: 'lastActivationTime',
    schema: {
      type: SchemaColumnType.DateTime,
    },
  },
  {
    name: 'activationCount',
    label: 'Checked number',
    isSortable: true,
  },
  {
    name: 'cmdletsGenerated',
    label: 'Cmdlets Generated',
    isSortable: true,
  },
  {
    name: 'state',
    label: 'Status',
    isSortable: true,
    width: '150px',
    filterRenderer: () => {
      return (
        <TableMultiSelectFilter<AdhRuleFilter, AdhRuleState> filterName="ruleStates" options={ruleStatesOptions} />
      );
    },
    filterName: 'ruleStates',
    schema: {
      cellRenderer: (rule: AdhRule) => {
        return <RuleStatusCell rule={rule} />;
      },
    },
  },
  {
    name: 'actions',
    label: 'Actions1',
    headerAlign: 'center',
    width: '120px',
    schema: {
      cellRenderer: (rule: AdhRule) => <RuleActionsCell rule={rule} />,
    },
  },
];
