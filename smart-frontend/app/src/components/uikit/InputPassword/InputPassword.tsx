/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React, { useState } from 'react';
import IconButton from '@uikit/IconButton/IconButton';
import type { InputProps } from '@uikit/Input/Input';
import Input from '@uikit/Input/Input';

interface PasswordFieldProps extends Omit<InputProps, 'type' | 'endAdornment' | 'startAdornment'> {
  areAsterisksShown?: boolean;
}

const dummyPasswordValue = '******';

const InputPassword = React.forwardRef<HTMLInputElement, PasswordFieldProps>(
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
            icon={showPassword ? 'eye' : 'eye-closed'}
            onClick={toggleShowPassword}
            disabled={props.disabled}
            variant="secondary"
            type="button"
          />
        }
      />
    );
  },
);

export default InputPassword;
