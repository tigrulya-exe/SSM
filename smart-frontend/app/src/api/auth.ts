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
import { httpClient } from './httpClient';
import { encode } from 'js-base64';

export class AuthApi {
  public static async login(username: string, password: string) {
    const response = await httpClient.get('/api/v2/rules', {
      headers: {
        Authorization: `Basic ${encode(`${username}:${password}`)}`,
      },
    });

    return response.data;
  }

  public static async logout() {
    const response = await httpClient.post('/api/v2/logout');
    return response.data;
  }

  public static async checkSession() {
    const response = await httpClient.get<{ name: string }>('/api/v2/rules');

    return response.data;
  }
}
