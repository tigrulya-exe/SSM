import React, { useEffect } from 'react';
import s from './NonAuthLayout.module.scss';
import HeaderToolbar from '@layouts/partials/HeaderToolbar/HeaderToolbar';
import NotificationsSideBar from '@layouts/partials/NotificationsSideBar/NotificationsSideBar';
import { useDispatch } from '@hooks';
import { cleanupNotifications } from '@store/notificationsSlice';
import PageContent from '@layouts/partials/PageContent/PageContent';
import MainLogo from '@layouts/partials/MainLogo/MainLogo';
import UnionLogo from './images/union.svg?react';

const NonAuthLayout: React.FC<React.PropsWithChildren> = ({ children }) => {
  const dispatch = useDispatch();

  useEffect(() => {
    return () => {
      dispatch(cleanupNotifications());
    };
  }, [dispatch]);

  return (
    <div className={s.nonAuthLayout}>
      <div className={s.nonAuthLayout__leftSide}>
        <div>
          <MainLogo height="40px" />
          <div className={s.nonAuthLayout__description}>
            Arenadata Smart Storage Management
            <br />
            Manage your data effectively?
          </div>
          <UnionLogo className={s.nonAuthLayout__union} />
        </div>
      </div>
      <PageContent className={s.nonAuthLayout__rightSide}>
        <header className={s.nonAuthLayout__header}>
          <HeaderToolbar />
        </header>
        <div className={s.nonAuthLayout__rightContent}>{children}</div>
      </PageContent>
      <NotificationsSideBar />
    </div>
  );
};

export default NonAuthLayout;
