import React from 'react';
import FlexGroup from '@uikit/FlexGroup/FlexGroup';
import { IconButton } from '@uikit';
import TableCell from '@uikit/Table/TableCell/TableCell';
import { useDispatch } from '@hooks';
import type { AdhRule } from '@models/adh';
import { openDeleteRuleDialog, openStartRuleDialog, openStopRuleDialog } from '@store/adh/rules/rulesActionsSlice';

interface RuleActionsCellProps {
  rule: AdhRule;
}

const RuleActionsCell: React.FC<RuleActionsCellProps> = ({ rule }) => {
  const dispatch = useDispatch();

  const handlePlay = () => {
    dispatch(openStartRuleDialog(rule));
  };
  const handlePause = () => {
    dispatch(openStopRuleDialog(rule));
  };
  const handleDelete = () => {
    dispatch(openDeleteRuleDialog(rule));
  };

  return (
    <TableCell align="center">
      <FlexGroup gap="4px">
        {rule.state === 'ACTIVE' ? (
          <IconButton icon="pause" title="Stop rule" onClick={handlePause} />
        ) : (
          <IconButton icon="play" title="Start rule" onClick={handlePlay} />
        )}
        <IconButton icon="delete" title="Delete rule" onClick={handleDelete} />
      </FlexGroup>
    </TableCell>
  );
};

export default RuleActionsCell;
