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
import type { HTMLAttributes } from 'react';
import React from 'react';
import cn from 'classnames';
import s from './Panel.module.scss';
import { firstUpperCase } from '@utils/stringUtils';

type PanelPaddingVariant = 'default' | 'empty' | 'small';

export interface PanelProps extends HTMLAttributes<HTMLDivElement> {
  variant?: 'primary' | 'secondary' | 'tertiary' | 'none';
  vPadding?: PanelPaddingVariant;
  hPadding?: PanelPaddingVariant;
}

const Panel: React.FC<PanelProps> = ({
  className,
  children,
  variant = 'primary',
  vPadding = 'default',
  hPadding = 'default',
  ...props
}) => {
  const classes = cn(
    s.panel,
    s[`panel_${variant}`],
    s[`panel_vPadding${firstUpperCase(vPadding)}`],
    s[`panel_hPadding${firstUpperCase(hPadding)}`],
    className,
  );
  return (
    <div className={classes} {...props}>
      {children}
    </div>
  );
};

export default Panel;
