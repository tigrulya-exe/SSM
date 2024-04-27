import React from 'react';
import cn from 'classnames';
import Icon from '@uikit/Icon/Icon';
import type { IconsNames } from '@uikit/Icon/sprite';
import s from './IconButton.module.scss';
import Button from '@uikit/Button/Button';

interface IconButtonProps extends Omit<React.ButtonHTMLAttributes<HTMLButtonElement>, 'children'> {
  icon: IconsNames;
  size?: number;
  variant?: 'primary' | 'secondary';
}

const IconButton = React.forwardRef<HTMLButtonElement, IconButtonProps>(
  ({ className, size = 12, icon, variant = 'primary', ...props }, ref) => {
    const classes = cn(className, s.iconButton, s[`iconButton_${variant}`]);

    if (variant === 'primary') {
      return (
        <Button className={classes} ref={ref} {...props} variant="tertiary">
          <Icon name={icon} size={size} />
        </Button>
      );
    }

    return (
      <button className={classes} ref={ref} {...props}>
        <Icon name={icon} size={size} />
      </button>
    );
  },
);

IconButton.displayName = 'IconButton';

export default IconButton;
