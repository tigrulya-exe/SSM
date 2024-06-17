import React from 'react';
import TableCell from '@uikit/Table/TableCell/TableCell';
import FlexGroup from '@uikit/FlexGroup/FlexGroup';
import StatusMarker from '@commonComponents/StatusMarker/StatusMarker';
import type { AdhRule, AdhRuleState } from '@models/adh';
import { getStatusLabel } from '@utils/humanisationUtils';
import type { Status } from '@commonComponents/StatusMarker/StatusMarker.types';

const roleStateToStatus: Record<AdhRuleState, Status> = {
  ACTIVE: 'online',
  DISABLED: 'offline',
};

interface RuleStatusCellProps {
  rule: AdhRule;
}

const RuleStatusCell: React.FC<RuleStatusCellProps> = ({ rule }) => {
  return (
    <TableCell>
      <FlexGroup gap="6px">
        <StatusMarker status={roleStateToStatus[rule.state]} />
        {getStatusLabel(rule.state)}
      </FlexGroup>
    </TableCell>
  );
};

export default RuleStatusCell;
