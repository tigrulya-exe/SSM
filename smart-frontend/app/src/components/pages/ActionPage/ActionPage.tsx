//import React from 'react';
import { AdhActionsApi } from '@api';

const ActionPage = () => {
  const action = AdhActionsApi.getAction('11');

  console.info('action = ', action);

  return <div>test</div>;
};

export default ActionPage;
