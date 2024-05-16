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
import type { ChangeEventHandler, RefObject } from 'react';
import React, { useRef } from 'react';
import cn from 'classnames';
import type { InputProps } from '@uikit/Input/Input';
import Input from '@uikit/Input/Input';
import { useForwardRef } from '@hooks/useForwardRef';
import InputNumberArrows from './InputNumberArrows/InputNumberArrows';
import { createChangeEvent } from '@utils/handlerUtils';
import s from './InputNumber.module.scss';

type InputNumberProps = Omit<InputProps, 'type' | 'endAdornment' | 'startAdornment'>;

const InputNumber = React.forwardRef<HTMLInputElement, InputNumberProps>(
  ({ className, onChange, disabled, readOnly, ...props }, ref) => {
    const localRef = useRef<HTMLInputElement>(null);
    const reference = useForwardRef(ref, localRef);

    const handleStepUp = () =>
      handleStep({
        direction: 'up',
        ref: localRef,
        onChange,
      });

    const handleStepDown = () =>
      handleStep({
        direction: 'down',
        ref: localRef,
        onChange,
      });

    return (
      <Input
        className={cn(className, s.inputNumber)}
        type="number"
        ref={reference}
        onChange={onChange}
        disabled={disabled}
        readOnly={readOnly}
        endAdornment={
          <InputNumberArrows onStepUp={handleStepUp} onStepDown={handleStepDown} disabled={disabled || readOnly} />
        }
        {...props}
      />
    );
  },
);

// InputNumber.displayName = 'InputNumber';

export default InputNumber;

interface HandleStepParams {
  direction: 'up' | 'down';
  ref: RefObject<HTMLInputElement> | null;
  onChange?: ChangeEventHandler;
  successCallback?: (val: string) => void;
}

const handleStep = ({ direction, ref, onChange, successCallback }: HandleStepParams) => {
  if (!ref?.current) return;

  const target = ref.current;
  const prevValue = target.value;

  // safari crashes when call stepUp/stepDown with unset value
  if (prevValue === '') {
    target.value = '0';
  }

  if (direction === 'up') {
    target.stepUp();
  } else {
    target.stepDown();
  }
  const result = target.value;

  successCallback && successCallback(result);

  if (result === prevValue || !onChange) return;

  const changeEvent = createChangeEvent(target);
  changeEvent.target.value = result;

  onChange(changeEvent);
};
