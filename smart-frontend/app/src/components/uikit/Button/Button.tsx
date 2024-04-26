import React from 'react';
import cn from 'classnames';
import s from './Button.module.scss';

type ButtonVariant = 'primary' | 'secondary' | 'tertiary';
type ButtonSize = 'medium' | 'small';

interface ButtonProps extends Omit<React.ButtonHTMLAttributes<HTMLButtonElement>, 'title'> {
  variant?: ButtonVariant;
  hasError?: boolean;
  size?: ButtonSize;
  title?: string;
}

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      children,
      className,
      disabled,
      variant = 'primary',
      size = 'medium',
      hasError = false,
      type = 'button',
      title,
      ...props
    },
    ref,
  ) => {
    const buttonClasses = cn(
      //
      className,
      s.button,
      s[`button_${variant}`],
      s[`button_${size}`],
      {
        [s.button_error]: hasError,
      },
    );

    return (
      <button
        //
        className={buttonClasses}
        disabled={disabled}
        type={type}
        {...props}
        ref={ref}
      >
        {children}
      </button>
    );
  },
);

Button.displayName = 'Button';

export default Button;
