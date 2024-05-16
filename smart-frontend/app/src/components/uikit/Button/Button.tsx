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
