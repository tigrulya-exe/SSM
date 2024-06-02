import React from 'react';
import type { HTMLAttributes } from 'react';
import s from './LeftBarMenu.module.scss';
import cn from 'classnames';

const LeftBarMenu: React.FC<HTMLAttributes<HTMLDivElement>> = ({ className, children, ...props }) => {
  return (
    <div className={cn(className, s.leftBarMenu)} {...props}>
      {children}
    </div>
  );
};

export default LeftBarMenu;
