import React, { useState } from 'react';
import IconButton from '@uikit/IconButton/IconButton';
import s from './PasswordInput.module.scss';
import type { InputProps } from '@uikit/Input/Input';
import Input from '@uikit/Input/Input';

interface PasswordFieldProps extends Omit<InputProps, 'type' | 'endAdornment' | 'startAdornment'> {
  areAsterisksShown?: boolean;
}

const dummyPasswordValue = '******';

const PasswordInput = React.forwardRef<HTMLInputElement, PasswordFieldProps>(
  ({ areAsterisksShown = false, placeholder, ...props }, ref) => {
    const [showPassword, setShowPassword] = useState(false);
    const [passwordPlaceholder, setPasswordPlaceholder] = useState(dummyPasswordValue);

    const toggleShowPassword = () => {
      setShowPassword((prev) => !prev);
    };

    const focusHandler = (e: React.FocusEvent<HTMLInputElement>) => {
      if (areAsterisksShown) {
        setPasswordPlaceholder('');
      }

      props.onFocus?.(e);
    };

    const blurHandler = (e: React.FocusEvent<HTMLInputElement>) => {
      if (areAsterisksShown) {
        setPasswordPlaceholder(dummyPasswordValue);
      }

      props.onBlur?.(e);
    };

    return (
      <Input
        {...props}
        ref={ref}
        type={showPassword ? 'text' : 'password'}
        placeholder={!areAsterisksShown ? placeholder : passwordPlaceholder}
        onFocus={focusHandler}
        onBlur={blurHandler}
        endAdornment={
          <IconButton
            className={s.passwordField__eye}
            icon={showPassword ? 'eye' : 'eye-closed'}
            onClick={toggleShowPassword}
            disabled={props.disabled}
            variant="secondary"
          />
        }
      />
    );
  },
);

export default PasswordInput;
