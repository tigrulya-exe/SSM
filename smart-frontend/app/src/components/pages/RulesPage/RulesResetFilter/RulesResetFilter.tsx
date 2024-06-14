import React from 'react';
import { Button } from '@uikit';
import { useDispatch } from '@hooks';
import { resetRulesFilter } from '@store/adh/rules/rulesTableSlice';

const RulesResetFilter: React.FC = () => {
  const dispatch = useDispatch();

  const handleClick = () => {
    dispatch(resetRulesFilter());
  };

  return (
    <Button onClick={handleClick} variant="secondary">
      Reset filter
    </Button>
  );
};

export default RulesResetFilter;
