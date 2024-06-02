import type { CSSProperties } from 'react';
import React, { useMemo } from 'react';
import cn from 'classnames';
import s from './FlexGroup.module.scss';

interface FlexGroupProps extends React.HTMLAttributes<HTMLDivElement> {
  gap?: CSSProperties['gap'];
}

const FlexGroup: React.FC<FlexGroupProps> = ({ children, className, gap, style, ...props }) => {
  const innerStyle = useMemo(() => ({ gap, ...style }), [gap, style]);

  return (
    <div className={cn(className, s.flexGroup)} style={innerStyle} {...props}>
      {children}
    </div>
  );
};

export default FlexGroup;
