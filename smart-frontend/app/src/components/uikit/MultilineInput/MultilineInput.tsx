import React from 'react';
import cn from 'classnames';
import s from './MultilineInput.module.scss';

type MultilineInputProps = React.HTMLProps<HTMLTextAreaElement>;

const MultilineInput: React.FC<MultilineInputProps> = ({ className, ...props }) => {
  return <textarea className={cn(className, s.multilineInput)} {...props} />;
};

export default MultilineInput;
