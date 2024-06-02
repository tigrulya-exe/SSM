import React from 'react';
import s from './MainLayout.module.scss';
import NotificationsSideBar from '@layouts/partials/NotificationsSideBar/NotificationsSideBar';
import MainHeader from '@layouts/partials/MainHeader/MainHeader';
import MainLeftSideBar from '@layouts/partials/MainLeftSideBar/MainLeftSideBar';

const MainLayout: React.FC<React.PropsWithChildren> = ({ children }) => {
  return (
    <div className={s.mainLayout}>
      <MainLeftSideBar />
      <div className={s.mainLayout__body}>
        <MainHeader />
        {children}
      </div>
      <NotificationsSideBar />
    </div>
  );
};

export default MainLayout;
