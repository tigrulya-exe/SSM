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
  role: AdhRule;
}

const RuleStatusCell: React.FC<RuleStatusCellProps> = ({ role }) => {
  return (
    <TableCell>
      <FlexGroup gap="6px">
        <StatusMarker status={roleStateToStatus[role.state]} />
        {getStatusLabel(role.state)}
      </FlexGroup>
    </TableCell>
  );
};

export default RuleStatusCell;
