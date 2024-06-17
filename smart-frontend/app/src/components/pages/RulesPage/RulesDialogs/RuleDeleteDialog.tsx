import React from 'react';
import { Dialog } from '@uikit';
import { useDispatch, useStore } from '@hooks';
import { closeCreateRuleDialog, deleteRuleWithUpdate } from '@store/adh/rules/rulesActionsSlice';

const RuleDeleteDialog: React.FC = () => {
  const dispatch = useDispatch();
  const rule = useStore(({ adh }) => adh.rulesActions.deleteDialog.rule);
  const isOpen = !!rule;

  const closeDialog = () => {
    dispatch(closeCreateRuleDialog());
  };
  const handleDelete = () => {
    if (rule) {
      dispatch(deleteRuleWithUpdate(rule.id));
    }
  };

  return (
    <Dialog
      isOpen={isOpen}
      onOpenChange={closeDialog}
      title="Are you sure you want to delete rule?"
      onAction={handleDelete}
      actionButtonLabel="Delete"
    ></Dialog>
  );
};

export default RuleDeleteDialog;
