import type { FieldProps } from './Field.types';
import s from './Field.module.scss';
import cn from 'classnames';

export const useFieldStyles = ({ size = 'medium', disabled, hasError }: FieldProps) => {
  const fieldClasses = cn(s.field, s[`field_${size}`], {
    [s.field_error]: hasError,
    [s.field_disabled]: disabled,
  });
  const fieldContentClasses = s.field__mainContent;

  return {
    fieldClasses,
    fieldContentClasses,
  };
};
