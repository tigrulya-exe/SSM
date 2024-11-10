import React from 'react';
import s from './SimpleLogView.module.scss';
import cn from 'classnames';
import Button from '../Button/Button';

interface LogViewProps {
  log: string;
  className?: string;
}

const SimpleLogView = ({ log, className }: LogViewProps) => {
  const copyHandler = () => {
    navigator.clipboard.writeText(log);
  };

  return (
    <div className={cn(s.simpleLogView, className)}>
      <Button size="small" onClick={copyHandler}>
        copy
      </Button>
      <div className={cn(s.simpleLogView__wrapper, 'scroll')}>
        <pre>{log}</pre>
      </div>
    </div>
  );
};

export default SimpleLogView;
