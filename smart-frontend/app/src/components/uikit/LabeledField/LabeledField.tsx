import type { ReactNode } from 'react';
import React from 'react';
import s from './LabeledField.module.scss';
import cn from 'classnames';

export interface LabeledFieldProps extends React.HTMLAttributes<HTMLDivElement> {
  label: ReactNode;
  disabled?: boolean;
  hint?: ReactNode;
  direction?: 'column' | 'row';
  dataTest?: string;
}

const LabeledField = React.forwardRef<HTMLDivElement, LabeledFieldProps>(
  ({ label, hint = '', className, disabled = false, direction = 'column', children, dataTest, ...props }, ref) => {
    const labeledFieldClasses = cn(
      s.labeledField,
      {
        [s.labeledField_disabled]: disabled,
        [s.labeledField_asRow]: direction === 'row',
      },
      className,
    );

    return (
      <div ref={ref} className={labeledFieldClasses} {...props}>
        <div className={s.labeledField__labelWrap}>
          <label className={s.labeledField__label}>{label}</label>
          {hint}
        </div>
        {children}
      </div>
    );
  },
);

export default LabeledField;
