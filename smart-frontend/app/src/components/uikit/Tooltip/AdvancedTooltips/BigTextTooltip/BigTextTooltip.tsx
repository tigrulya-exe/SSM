import React from 'react';
import s from './BigTextTooltip.module.scss';
import Tooltip, { type TooltipProps } from '../../Tooltip';
import { prepareLabel } from './BigTextTooltip.utils';

interface BigTextTooltipProps extends TooltipProps {
  label: string;
  labelLimit?: number;
}

const BigTextTooltip = ({
  children,
  label,
  labelLimit = Number.POSITIVE_INFINITY,
  ...otherProps
}: BigTextTooltipProps) => {
  const preparedLabel = prepareLabel(label, labelLimit);

  return (
    <Tooltip label={preparedLabel} placement="top-start" className={s.bigTextTooltip} {...otherProps}>
      {children}
    </Tooltip>
  );
};

export default BigTextTooltip;
