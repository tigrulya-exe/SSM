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
export enum NotificationVariant {
  Error = 'Error',
  Info = 'Info',
  Success = 'Success',
}

export interface BaseNotification<Model> {
  id: string;
  variant: NotificationVariant;
  ttl?: number; // in milliseconds
  isDisabledClose?: boolean;
  model: Model;
}

export interface ErrorNotification extends BaseNotification<{ message: string }> {
  variant: NotificationVariant.Error;
}

export interface InfoNotification extends BaseNotification<{ message: string }> {
  variant: NotificationVariant.Info;
}

export interface SuccessNotification extends BaseNotification<{ message: string }> {
  variant: NotificationVariant.Success;
}

export type Notification = ErrorNotification | InfoNotification | SuccessNotification;
