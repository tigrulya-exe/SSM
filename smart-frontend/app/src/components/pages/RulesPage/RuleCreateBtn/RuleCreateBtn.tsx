import React from 'react';
import { Button } from '@uikit';
import { useDispatch, useStore } from '@hooks';
import { openCreateRuleDialog } from '@store/adh/rules/rulesActionsSlice';

const RuleCreateBtn: React.FC = () => {
  const dispatch = useDispatch();
  const isActionInProgress = useStore(({ adh }) => adh.rulesActions.isActionInProgress);

  const handleClick = () => {
    dispatch(openCreateRuleDialog());
  };

  return (
    <Button onClick={handleClick} disabled={isActionInProgress}>
      Create rule
    </Button>
  );
};

export default RuleCreateBtn;
