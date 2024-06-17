import React from 'react';
import { Dialog } from '@uikit';
import { useDispatch, useStore } from '@hooks';
import { closeStopRuleDialog, stopRule } from '@store/adh/rules/rulesActionsSlice';

const RuleStopDialog: React.FC = () => {
  const dispatch = useDispatch();
  const rule = useStore(({ adh }) => adh.rulesActions.stopDialog.rule);
  const isOpen = !!rule;

  const closeDialog = () => {
    dispatch(closeStopRuleDialog());
  };
  const handleDelete = () => {
    if (rule) {
      dispatch(stopRule(rule.id));
    }
  };

  return (
    <Dialog
      isOpen={isOpen}
      onOpenChange={closeDialog}
      title="Are you sure you want to stop rule?"
      onAction={handleDelete}
      actionButtonLabel="Stop"
    ></Dialog>
  );
};

export default RuleStopDialog;
