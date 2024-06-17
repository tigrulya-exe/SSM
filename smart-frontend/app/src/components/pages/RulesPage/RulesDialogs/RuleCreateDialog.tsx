import React, { useEffect, useState } from 'react';
import FooterDialog from '@uikit/FooterDialog/FooterDialog';
import { useDispatch, useStore } from '@hooks';
import { closeCreateRuleDialog, createRuleWithUpdate } from '@store/adh/rules/rulesActionsSlice';
import { MultilineInput } from '@uikit';

const RuleCreateDialog: React.FC = () => {
  const dispatch = useDispatch();
  const isOpen = useStore(({ adh }) => adh.rulesActions.createDialog.isOpen);

  const [ruleText, setRuleText] = useState('');

  useEffect(() => {
    // clear when close dialog
    if (!isOpen) {
      setRuleText('');
    }
  }, [isOpen, setRuleText]);

  const closeDialog = () => {
    dispatch(closeCreateRuleDialog());
  };

  const handleChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    setRuleText(event.target.value);
  };

  const handleCreate = () => {
    dispatch(createRuleWithUpdate(ruleText));
  };

  return (
    <FooterDialog
      isOpen={isOpen}
      onOpenChange={closeDialog}
      title="Create Rule"
      actionButtonLabel="Create"
      onAction={handleCreate}
    >
      <MultilineInput value={ruleText} onChange={handleChange} />
    </FooterDialog>
  );
};

export default RuleCreateDialog;
