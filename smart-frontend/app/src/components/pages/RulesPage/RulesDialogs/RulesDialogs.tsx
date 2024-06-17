import React from 'react';
import RuleCreateDialog from './RuleCreateDialog';
import RuleDeleteDialog from './RuleDeleteDialog';
import RuleStartDialog from './RuleStartDialog';
import RuleStopDialog from './RuleStopDialog';

const RulesDialogs: React.FC = () => {
  return (
    <>
      <RuleCreateDialog />
      <RuleDeleteDialog />
      <RuleStartDialog />
      <RuleStopDialog />
    </>
  );
};

export default RulesDialogs;
