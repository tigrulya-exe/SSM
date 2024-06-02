import React from 'react';
import LeftBarMenuItem from './LeftBarMenuItem';
import { Tooltip } from '@uikit';
import ConditionalWrapper from '@uikit/ConditionalWrapper/ConditionalWrapper';

const LeftBarUserItem: React.FC = () => {
  // Todo: use name from store
  const userName = 'AdminAdminAdminAdmin';
  const isShowTooltip = userName.length > 12;

  return (
    <ConditionalWrapper Component={Tooltip} isWrap={isShowTooltip} label={userName}>
      <LeftBarMenuItem icon="user" label={userName} />
    </ConditionalWrapper>
  );
};

export default LeftBarUserItem;
