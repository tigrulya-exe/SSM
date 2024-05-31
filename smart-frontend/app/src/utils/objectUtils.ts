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
export const getValueByPath = (object: unknown, path: string) => {
  const value = path.split('.').reduce((acc, c) => acc && acc[c as keyof typeof object], object);
  return value;
};

export const isObject = (obj: unknown) => Object.prototype.toString.call(obj) === '[object Object]';

type Scalar = number | string | boolean;
type Structure = object | object[] | Scalar;

export const structureTraversal = (structure: Structure, callback?: (subItem: Structure) => Structure): Structure => {
  if (Array.isArray(structure)) {
    return structure.map((item) => structureTraversal(item, callback));
  }
  if (isObject(structure)) {
    return Object.entries(structure).reduce(
      (res, [key, val]) => {
        res[key] = structureTraversal(val, callback);
        return res;
      },
      {} as Record<string, Structure>,
    );
  }

  return callback ? callback(structure) : structure;
};

export const objectTrim = (object: Structure) =>
  structureTraversal(object, (item) => (typeof item === 'string' ? item.trim() : item));

export const areAllFieldsUndefined = (errors: object) => !Object.values(errors).some((v) => !!v);
