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
import type { RangePreset } from './DateRangePicker.types';

export const defaultRangesPreset: RangePreset[] = [
  { id: 'now-1h', description: 'last 1 hour' },
  { id: 'now-2h', description: 'last 2 hours' },
  { id: 'now-4h', description: 'last 4 hours' },
  { id: 'now-8h', description: 'last 8 hours' },
  { id: 'now-12h', description: 'last 12 hours' },
  { id: 'now-24h', description: 'last 24 hours' },
  { id: 'now-2d', description: 'last 2 days' },
  { id: 'now-5d', description: 'last 5 days' },
  { id: 'now-7d', description: 'last 7 days' },
  { id: 'now-14d', description: 'last 2 weeks' },
  { id: 'now-1M', description: 'last month' },
];
