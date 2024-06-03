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
import { generatePath } from 'react-router-dom';
import type { BreadcrumbsItemConfig, DynamicParameters, Route } from '@routes/routes.types';
import routes from '@routes/routes';

const buildPathBreadcrumbs = (path: string, dynamicParams: DynamicParameters): BreadcrumbsItemConfig[] => {
  const parts = routes[path];
  if (!parts) {
    console.error(`breadcrumbs are not defined for path ${path}`);
    return [];
  }

  const breadcrumbs: BreadcrumbsItemConfig[] = [];

  for (const part of parts.breadcrumbs) {
    const crumbs = {} as BreadcrumbsItemConfig;

    if (part.href) {
      crumbs.href = generatePath(part.href, dynamicParams);
    }

    if (part.label) {
      crumbs.label = generatePath(part.label, dynamicParams);
    }

    if (Object.keys(crumbs).length) {
      breadcrumbs.push(crumbs);
    }
  }

  return breadcrumbs;
};

export const buildBreadcrumbs = (route: Route): BreadcrumbsItemConfig[] => {
  return buildPathBreadcrumbs(route.path, route.params);
};
