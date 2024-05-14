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
import type { InputProps } from '@uikit/Input/Input';
import Input from '@uikit/Input/Input';
import IconButton from '@uikit/IconButton/IconButton';
import cn from 'classnames';
import s from './CommonSelectField.module.scss';

type CommonSelectFieldProps = Omit<InputProps, 'endAdornment' | 'startAdornment' | 'readOnly' | 'onClick'> & {
  onClick: () => void;
  onClear: () => void;
  isOpen: boolean;
};

const CommonSelectField = React.forwardRef<HTMLInputElement, CommonSelectFieldProps>(
  ({ className, onClick, onClear, isOpen, hasError, disabled, ...props }, ref) => {
    const classes = cn(className, s.commonSelectField, { 'is-active': isOpen });

    const handleClick = () => {
      onClick?.();
    };

    return (
      <>
        <Input
          //
          {...props}
          className={classes}
          endAdornment={
            !disabled && (
              <>
                <IconButton icon="chevron" onClick={handleClick} size={12} variant="secondary" />
              </>
            )
          }
          readOnly={true}
          onClick={handleClick}
          ref={ref}
          hasError={hasError}
          disabled={disabled}
        />
      </>
    );
  },
);
export default CommonSelectField;

CommonSelectField.displayName = 'CommonSelectField';
