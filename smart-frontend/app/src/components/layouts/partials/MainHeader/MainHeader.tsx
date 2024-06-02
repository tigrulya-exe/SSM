import React from 'react';
import s from './MainHeader.module.scss';
import HeaderToolbar from '@layouts/partials/HeaderToolbar/HeaderToolbar';
import Breadcrumbs from '@layouts/partials/Breadcrumbs/Breadcrumbs';

const MainHeader: React.FC = () => {
  return (
    <header className={s.mainHeader} data-test="header-container">
      <Breadcrumbs />
      <HeaderToolbar />
    </header>
  );
};

export default MainHeader;
