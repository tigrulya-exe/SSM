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
import { Button } from '@uikit';

import s from './Alert.module.scss';
import type { AlertOptions } from './Alert.types';

interface AlertProps extends AlertOptions {
  isDisabledClose?: boolean;
}

const Alert: React.FC<AlertProps> = ({ children, className, onClose, isDisabledClose = false }) => {
  return (
    <div className={cn(className, s.alert)}>
      <div>{children}</div>
      <Button className={s.alert__button} size="small" variant="secondary" onClick={onClose} disabled={isDisabledClose}>
        OK
      </Button>
    </div>
  );
};

export default Alert;
