import React from 'react';
import s from './MainLeftSideBar.module.scss';
import LeftBarMenu from '@layouts/partials/LeftBarMenu/LeftBarMenu';
import LeftBarMenuItem from '@layouts/partials/LeftBarMenu/LeftBarMenuItem';
import MainLogo from '@layouts/partials/MainLogo/MainLogo';
import LeftBarLogoutItem from '@layouts/partials/LeftBarMenu/LeftBarLogoutItem';
import LeftBarUserItem from '@layouts/partials/LeftBarMenu/LeftBarUserItem';

const MainLeftSideBar: React.FC = () => {
  return (
    <aside className={s.mainLeftSideBar}>
      <MainLogo />
      <LeftBarMenu>
        <LeftBarMenuItem icon="cluster-info" label="Cluster Info" to="/" />
        <LeftBarMenuItem icon="rules" label="Rules" to="/rules" />
        <LeftBarMenuItem icon="actions" label="Actions" to="/actions" />
        <LeftBarMenuItem icon="audit" label="Audit" to="/audit" />
      </LeftBarMenu>

      <LeftBarMenu className={s.mainLeftSideBar__systemMenu}>
        <LeftBarMenuItem icon="documentation" label="Documentation" to="/documentation" />
        <LeftBarUserItem />
        <LeftBarLogoutItem />
      </LeftBarMenu>
    </aside>
  );
};

export default MainLeftSideBar;
