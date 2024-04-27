import React from 'react';
import cn from 'classnames';
import type { IconsNames } from './sprite';

interface IconProps extends React.SVGAttributes<SVGSVGElement> {
  size?: number;
  name: IconsNames;
}

const Icon = React.forwardRef<SVGSVGElement, IconProps>(({ name, size = 12, className, ...props }, ref) => {
  const classString = cn('icon', className);

  return (
    <svg className={classString} width={size} height={size} {...props} ref={ref}>
      <use xlinkHref={`#${name}`} />
    </svg>
  );
});

Icon.displayName = 'Icon';

export default Icon;
