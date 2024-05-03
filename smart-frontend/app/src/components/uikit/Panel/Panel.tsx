import type { HTMLAttributes } from 'react';
import React from 'react';
import cn from 'classnames';
import s from './Panel.module.scss';
import { firstUpperCase } from '@utils/stringUtils';

type PanelPaddingVariant = 'default' | 'empty' | 'small';

export interface PanelProps extends HTMLAttributes<HTMLDivElement> {
  variant?: 'primary' | 'secondary' | 'tertiary' | 'none';
  vPadding?: PanelPaddingVariant;
  hPadding?: PanelPaddingVariant;
}

const Panel: React.FC<PanelProps> = ({
  className,
  children,
  variant = 'primary',
  vPadding = 'default',
  hPadding = 'default',
  ...props
}) => {
  const classes = cn(
    s.panel,
    s[`panel_${variant}`],
    s[`panel_vPadding${firstUpperCase(vPadding)}`],
    s[`panel_hPadding${firstUpperCase(hPadding)}`],
    className,
  );
  return (
    <div className={classes} {...props}>
      {children}
    </div>
  );
};

export default Panel;
