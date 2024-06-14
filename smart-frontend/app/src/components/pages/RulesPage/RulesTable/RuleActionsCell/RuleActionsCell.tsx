import React from 'react';
import FlexGroup from '@uikit/FlexGroup/FlexGroup';
import { IconButton } from '@uikit';
import TableCell from '@uikit/Table/TableCell/TableCell';

const RuleActionsCell: React.FC = () => {
  return (
    <TableCell align="center">
      <FlexGroup gap="4px">
        <IconButton icon="play" title="Run rule" />
        <IconButton icon="delete" title="Delete rule" />
      </FlexGroup>
    </TableCell>
  );
};

export default RuleActionsCell;
