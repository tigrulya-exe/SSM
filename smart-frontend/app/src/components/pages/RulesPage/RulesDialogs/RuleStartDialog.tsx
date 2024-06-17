import React from 'react';
import { Dialog } from '@uikit';
import { useDispatch, useStore } from '@hooks';
import { closeStartRuleDialog, startRule } from '@store/adh/rules/rulesActionsSlice';

const RuleStartDialog: React.FC = () => {
  const dispatch = useDispatch();
  const rule = useStore(({ adh }) => adh.rulesActions.startDialog.rule);
  const isOpen = !!rule;

  const closeDialog = () => {
    dispatch(closeStartRuleDialog());
  };
  const handleDelete = () => {
    if (rule) {
      dispatch(startRule(rule.id));
    }
  };

  return (
    <Dialog
      isOpen={isOpen}
      onOpenChange={closeDialog}
      title="Are you sure you want to start rule?"
      onAction={handleDelete}
      actionButtonLabel="Start"
    ></Dialog>
  );
};

export default RuleStartDialog;
