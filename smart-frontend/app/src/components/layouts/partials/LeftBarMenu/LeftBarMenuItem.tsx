import React from 'react';
import type { To } from 'react-router-dom';
import { NavLink } from 'react-router-dom';
import type { IconsNames } from '@uikit';
import { Icon } from '@uikit';
import s from './LeftBarMenu.module.scss';

type LeftBarMenuItemProps = {
  icon: IconsNames;
  label?: React.ReactNode;
} & (
  | {
      to?: To;
      onClick?: never;
    }
  | {
      to?: never;
      onClick?: () => void;
    }
);

const LeftBarMenuItem: React.FC<LeftBarMenuItemProps> = ({ icon, label, to, onClick }) => {
  const itemContent = (
    <>
      <Icon name={icon} size={20} />
      <span className={s.leftBarMenuItem__label}>{label}</span>
    </>
  );

  if (to) {
    return (
      <NavLink to={to} className={s.leftBarMenuItem}>
        {itemContent}
      </NavLink>
    );
  }

  return (
    <button className={s.leftBarMenuItem} onClick={onClick}>
      {itemContent}
    </button>
  );
};

export default LeftBarMenuItem;
