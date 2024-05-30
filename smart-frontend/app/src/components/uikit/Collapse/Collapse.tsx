import React from 'react';
import cn from 'classnames';
import s from './Collapse.module.scss';

interface CollapseProps extends React.HTMLAttributes<HTMLDivElement> {
  isExpanded: boolean;
}

const Collapse: React.FC<CollapseProps> = ({ isExpanded, children, className, ...props }) => {
  const classes = cn(className, s.collapse, { 'is-open': isExpanded });

  return (
    <div className={classes} {...props}>
      <div className={s.collapse__inner}>{children}</div>
    </div>
  );
};

export default Collapse;
