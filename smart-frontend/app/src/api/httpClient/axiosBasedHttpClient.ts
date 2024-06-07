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
import type { AxiosInstance, AxiosResponse, AxiosError } from 'axios';
import axios from 'axios';
import { RequestError } from './HttpClient';
import type { HttpClient, RequestOptions, Response } from './HttpClient';

export class AxiosBasedHttpClient implements HttpClient {
  protected axiosInstance: AxiosInstance;

  constructor() {
    this.axiosInstance = axios.create({
      baseURL: '/',
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json',
      },
      xsrfCookieName: 'csrftoken',
      xsrfHeaderName: 'X-CSRFToken',
    });
  }

  public async get<T>(url: string, options?: RequestOptions): Promise<Response<T>> {
    try {
      const axiosResponse = await this.axiosInstance.get<T>(url, options || {});
      const response = this.mapResponse<T>(axiosResponse);
      return response;
    } catch (axiosError) {
      const error = this.mapError(axiosError as AxiosError);
      throw error;
    }
  }

  public async post<T, D = unknown>(url: string, data?: D, options?: RequestOptions): Promise<Response<T>> {
    try {
      const axiosResponse = await this.axiosInstance.post<T>(url, data, options || {});
      const response = this.mapResponse<T>(axiosResponse);
      return response;
    } catch (axiosError) {
      const error = this.mapError(axiosError as AxiosError);
      throw error;
    }
  }

  public async put<T, D = unknown>(url: string, data: D, options?: RequestOptions): Promise<Response<T>> {
    try {
      const axiosResponse = await this.axiosInstance.put<T>(url, data, options || {});
      const response = this.mapResponse<T>(axiosResponse);
      return response;
    } catch (axiosError) {
      const error = this.mapError(axiosError as AxiosError);
      throw error;
    }
  }

  public async patch<T, D = unknown>(url: string, data?: D, options?: RequestOptions): Promise<Response<T>> {
    try {
      const axiosResponse = await this.axiosInstance.patch<T>(url, data, options || {});
      const response = this.mapResponse<T>(axiosResponse);
      return response;
    } catch (axiosError) {
      const error = this.mapError(axiosError as AxiosError);
      throw error;
    }
  }

  public async delete<T>(url: string, options?: RequestOptions): Promise<Response<T>> {
    try {
      const axiosResponse = await this.axiosInstance.delete(url, options || {});
      const response = this.mapResponse<T>(axiosResponse);
      return response;
    } catch (axiosError) {
      const error = this.mapError(axiosError as AxiosError);
      throw error;
    }
  }

  protected mapResponse<T>(axiosResponse: AxiosResponse): Response<T> {
    return {
      data: axiosResponse.data as T,
      headers: axiosResponse.headers as Response<T>['headers'],
      status: axiosResponse.status,
      statusText: axiosResponse.statusText,
    };
  }

  protected mapError(axiosError: AxiosError) {
    const response = axiosError.response ? this.mapResponse<unknown>(axiosError.response) : undefined;
    const error = new RequestError(axiosError.message, response);
    return error;
  }
}
