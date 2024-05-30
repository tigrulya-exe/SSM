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
import s from './Spinner.module.scss';
import cn from 'classnames';

export const SpinnerPanel: React.FC<SpinnerProps> = ({ className, ...props }) => {
  return (
    <div className={cn(s.spinnerPanel, className)}>
      <Spinner {...props} />
    </div>
  );
};

interface SpinnerProps {
  size?: number;
  className?: string;
}

const Spinner: React.FC<SpinnerProps> = ({ className, size = 40 }) => {
  const style = { width: size, height: size };
  return (
    <div className={cn(className, s.spinner)}>
      <svg viewBox="0 0 40 40" style={style}>
        <g fill="none" stroke="currentColor" strokeMiterlimit="10">
          <polygon
            className={s.polygon_1}
            strokeWidth="1.2"
            points="29.5,36.5 10.5,36.5 1,20 10.5,3.5 29.5,3.5 39,20"
          ></polygon>
          <polygon
            className={s.polygon_2}
            strokeWidth="0.5"
            points="26.3,31 13.7,31 7.3,20 13.7,9 26.3,9 32.7,20"
          ></polygon>
        </g>
      </svg>
    </div>
  );
};

export default Spinner;
