import React, { useState } from 'react';
import LeftBarMenuItem from './LeftBarMenuItem';
import { Dialog } from '@uikit';

const LeftBarLogoutItem: React.FC = () => {
  const [isOpenConfirm, setIsOpenConfirm] = useState(false);

  const handleLogout = () => {
    // dispatch(logout());
    setIsOpenConfirm(false);
  };

  return (
    <>
      <LeftBarMenuItem icon="logout" label="Log Out" onClick={() => setIsOpenConfirm(true)} />
      <Dialog
        isOpen={isOpenConfirm}
        onOpenChange={setIsOpenConfirm}
        width="440px"
        title="Are you sure you want to log out?"
        onAction={handleLogout}
      />
    </>
  );
};

export default LeftBarLogoutItem;
