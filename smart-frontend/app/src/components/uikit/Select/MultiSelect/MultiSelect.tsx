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
import React, { useMemo, useRef, useState } from 'react';
import type { InputProps } from '@uikit/Input/Input';
import MultiSelectPanel from './MultiSelectPanel/MultiSelectPanel';
import Popover from '@uikit/Popover/Popover';
import type { MultiSelectOptions } from '@uikit/Select/Select.types';
import { useForwardRef } from '@hooks/useForwardRef';
import CommonSelectField from '@uikit/Select/CommonSelect/CommonSelectField/CommonSelectField';
import PopoverPanelDefault from '@uikit/Popover/PopoverPanelDefault/PopoverPanelDefault';
import type { PopoverOptions } from '@uikit/Popover/Popover.types';

export type MultiSelectProps<T> = MultiSelectOptions<T> &
  PopoverOptions &
  Omit<InputProps, 'endAdornment' | 'startAdornment' | 'readOnly' | 'onChange' | 'value'>;

function MultiSelectComponent<T>(
  {
    options,
    value,
    onChange,
    disabled,
    checkAllLabel,
    maxHeight,
    hasError,
    isSearchable,
    searchPlaceholder,
    containerRef,
    placeholder,
    placement,
    offset,
    dependencyWidth = 'min-parent',
    ...props
  }: MultiSelectProps<T>,
  ref: React.ForwardedRef<HTMLInputElement>,
) {
  const [isOpen, setIsOpen] = useState(false);
  const localContainerRef = useRef(null);
  const containerReference = useForwardRef(localContainerRef, containerRef);

  const handleChange = (values: T[]) => {
    onChange?.(values);
  };

  const selectedOptionLabel = useMemo(() => {
    if (value.length === 0) {
      return '';
    }

    if (value.length === 1) {
      const option = options.find(({ value: val }) => val === value[0]);
      return option?.label;
    }

    return `${value.length} selected`;
  }, [options, value]);

  return (
    <>
      <CommonSelectField
        {...props}
        ref={ref}
        onClick={() => setIsOpen((prev) => !prev)}
        onClear={() => handleChange([])}
        isOpen={isOpen}
        value={selectedOptionLabel}
        containerRef={containerReference}
        hasError={hasError}
        disabled={disabled}
        placeholder={placeholder}
      />
      <Popover
        isOpen={isOpen}
        onOpenChange={setIsOpen}
        triggerRef={localContainerRef}
        dependencyWidth={dependencyWidth}
        placement={placement}
        offset={offset}
      >
        <PopoverPanelDefault>
          <MultiSelectPanel
            options={options}
            value={value}
            onChange={handleChange}
            checkAllLabel={checkAllLabel}
            maxHeight={maxHeight}
            isSearchable={isSearchable}
            searchPlaceholder={searchPlaceholder}
          />
        </PopoverPanelDefault>
      </Popover>
    </>
  );
}

const MultiSelect = React.forwardRef(MultiSelectComponent) as <T>(
  _props: MultiSelectProps<T>,
  _ref: React.ForwardedRef<HTMLInputElement>,
) => ReturnType<typeof MultiSelectComponent>;

export default MultiSelect;
