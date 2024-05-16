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
import IconButton from '@uikit/IconButton/IconButton';

import s from './InputNumberArrows.module.scss';

interface InputNumberArrowsProps {
  disabled?: boolean;
  onStepUp: () => void;
  onStepDown: () => void;
}

const InputNumberArrows: React.FC<InputNumberArrowsProps> = ({ disabled, onStepUp, onStepDown }) => {
  return (
    <div className={s.inputNumberArrows}>
      <IconButton
        icon="chevron"
        size={10}
        onClick={onStepUp}
        tabIndex={-1}
        disabled={disabled}
        className={s.inputNumberArrows__arrowUp}
        variant="secondary"
      />
      <IconButton
        //
        icon="chevron"
        size={10}
        onClick={onStepDown}
        tabIndex={-1}
        disabled={disabled}
        variant="secondary"
      />
    </div>
  );
};

export default InputNumberArrows;
