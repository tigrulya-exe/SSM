import type { TableColumnSchema } from '@uikit/Table/Table.types';
import { SchemaColumnType } from '@uikit/Table/Table.types';
import TableCell from '@uikit/Table/TableCell/TableCell';
import FlexGroup from '@uikit/FlexGroup/FlexGroup';
import { IconButton } from '@uikit';
import { TableDateRangePickerFilter, TableMultiSelectFilter, TableSearchFilter } from '@uikit/Table/TableFilter';
import type { AdhRule, AdhRuleFilter } from '@models/adh';
import { AdhRuleState } from '@models/adh';
import RuleStatusCell from '@pages/RulesPage/RulesTable/RuleStatusCell/RuleStatusCell';
import { getOptionsFromEnum } from '@uikit/Select/Select.utils';

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
        <TableMultiSelectFilter<AdhRuleFilter, AdhRuleState>
          filterName="ruleStates"
          options={getOptionsFromEnum(AdhRuleState)}
        />
      );
    },
    filterName: 'ruleStates',
    schema: {
      cellRenderer: (role: AdhRule) => {
        return <RuleStatusCell role={role} />;
      },
    },
  },
  {
    name: 'actions',
    label: 'Actions',
    headerAlign: 'center',
    width: '120px',
    schema: {
      cellRenderer: () => (
        <TableCell align="center">
          <FlexGroup gap="4px">
            <IconButton icon="play" />
            <IconButton icon="delete" />
          </FlexGroup>
        </TableCell>
      ),
    },
  },
];
