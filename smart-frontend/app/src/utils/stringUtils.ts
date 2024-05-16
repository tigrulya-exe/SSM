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
export const firstUpperCase = (str: string) => (str[0] ?? '').toUpperCase().concat(str.slice(1));

export const snakeToCamelCase = (name: string) => {
  const [firstPart, ...otherParts] = name.toLowerCase().split('_');

  return firstPart.concat(otherParts.map((part) => firstUpperCase(part)).join(''));
};

export const camelToSnakeCase = (name: string) => name.replace(/([A-Z])/g, '_$1').toLowerCase();

export const prettifyJson = (jsonString: string, space = 4) => {
  try {
    return JSON.stringify(JSON.parse(jsonString), null, space);
  } catch {
    return jsonString;
  }
};
