import React from 'react';
import type { Status } from './StatusMarker.types';
import cn from 'classnames';
import s from './StatusMarker.module.scss';

interface StatusMarkerProps extends React.HTMLAttributes<HTMLDivElement> {
  status: Status;
}

const StatusMarker: React.FC<StatusMarkerProps> = ({ status, className, ...props }) => {
  const classes = cn(className, s.statusMarker, s[`statusMarker_${status}`]);
  return <div className={classes} {...props}></div>;
};

export default StatusMarker;
