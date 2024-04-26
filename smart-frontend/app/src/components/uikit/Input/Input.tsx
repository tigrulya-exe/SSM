import React, { useRef } from 'react';
import cn from 'classnames';
import type { FieldProps } from '@uikit/Field/Field.types';
import { useFieldStyles } from '@uikit/Field/useFieldStyles';
import { useForwardRef } from '@hooks/useForwardRef';
import s from './Input.module.scss';

export interface InputProps extends FieldProps, Omit<React.InputHTMLAttributes<HTMLInputElement>, 'size'> {
  endAdornment?: React.ReactNode;
  startAdornment?: React.ReactNode;
  containerRef?: React.Ref<HTMLLabelElement>;
}

const Input = React.forwardRef<HTMLInputElement, InputProps>(
  (
    {
      className,
      size,
      hasError = false,
      startAdornment = null,
      endAdornment = null,
      disabled,
      containerRef,
      style,
      ...props
    },
    ref,
  ) => {
    const localRef = useRef<HTMLInputElement>(null);
    const reference = useForwardRef(ref, localRef);
    const { fieldClasses, fieldContentClasses: inputClasses } = useFieldStyles({ size, hasError, disabled });

    return (
      <label className={cn(className, fieldClasses, s.input)} ref={containerRef} style={style}>
        {startAdornment}
        <input ref={reference} className={inputClasses} {...props} disabled={disabled} />
        {endAdornment}
      </label>
    );
  },
);

Input.displayName = 'Input';

export default Input;
