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
import s from '../FormField.module.scss';
import Tooltip from '@uikit/Tooltip/Tooltip';
import cn from 'classnames';
import { Icon } from '@uikit';

interface FormFieldHintProps {
  description?: React.ReactNode;
  hasError?: boolean;
}
const FormFieldHint: React.FC<FormFieldHintProps> = ({ description, hasError }) => {
  return (
    <>
      {hasError && <Icon name="status-error" className={s.formField__marker} size={16} />}
      {description && (
        <Tooltip label={description} placement="top-start">
          <Icon name="status-info" className={cn(s.formField__marker, s.formField__marker_info)} size={16} />
        </Tooltip>
      )}
    </>
  );
};

export default FormFieldHint;
