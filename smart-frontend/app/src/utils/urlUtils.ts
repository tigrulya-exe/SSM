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
import type { To } from 'react-router-dom';
import { matchPath } from 'react-router-dom';

export const isCurrentPathname = (pathname: string, to: To | string, subPattern?: string): boolean => {
  if (matchPath(subPattern || '', pathname)) return true;

  const toString = typeof to === 'string' ? to : to.pathname || '';

  // if `to` - is path of root then full compare with pathname
  if (toString.startsWith('/')) {
    return to === pathname;
  }

  // if `to` - is relative link, check with end of pathname
  return pathname.endsWith(toString);
};

export const isCurrentParentPage = (pathname: string, subPage: To | string): boolean => {
  const toString = typeof subPage === 'string' ? subPage : subPage.pathname || '';
  const [, firstPart] = pathname.split('/');

  if (firstPart) {
    return toString === `/${firstPart}`;
  }

  return false;
};
