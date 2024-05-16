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
import type { IconsNames } from './sprite';

interface IconProps extends React.SVGAttributes<SVGSVGElement> {
  size?: number;
  name: IconsNames;
}

const Icon = React.forwardRef<SVGSVGElement, IconProps>(({ name, size = 12, className, ...props }, ref) => {
  const classString = cn('icon', className);

  return (
    <svg className={classString} width={size} height={size} {...props} ref={ref}>
      <use xlinkHref={`#${name}`} />
    </svg>
  );
});

Icon.displayName = 'Icon';

export default Icon;
