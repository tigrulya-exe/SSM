import React from 'react';
import { FloatingDelayGroup } from '@floating-ui/react';

export type TooltipGroupProps = React.ComponentProps<typeof FloatingDelayGroup>;

const TooltipGroup: React.FC<TooltipGroupProps> = (props) => {
  return <FloatingDelayGroup {...props}></FloatingDelayGroup>;
};

export default TooltipGroup;
