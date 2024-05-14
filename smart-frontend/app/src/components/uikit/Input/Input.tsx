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
