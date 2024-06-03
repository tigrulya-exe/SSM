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
import { useMemo } from 'react';
import { useLocation, matchRoutes } from 'react-router-dom';
import routes from '@routes/routes';

const pagesRoutes = Object.keys(routes).map((path: string) => ({ path }));

export const useCurrentRoute = () => {
  const { pathname } = useLocation();

  const currentRoute = useMemo(() => {
    const matchedRoutes = matchRoutes(pagesRoutes, pathname);
    if (!matchedRoutes?.length || !matchedRoutes[0]?.route.path) return;

    return {
      path: matchedRoutes[0].route.path,
      params: matchedRoutes[0].params,
    };
  }, [pathname]);

  return currentRoute;
};
