import React from 'react';
import CurrentDate from '@layouts/partials/CurrentDate/CurrentDate';
import ThemeSwitcher from '@layouts/partials/ThemeSwitcher/ThemeSwitcher';
import s from './HeaderToolbar.module.scss';

const HeaderToolbar: React.FC<React.PropsWithChildren> = () => {
  return (
    <div className={s.headerToolbar}>
      <CurrentDate />
      <ThemeSwitcher />
    </div>
  );
};

export default HeaderToolbar;
