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
interface MemoryUnitProps {
  number: number;
  label: string;
}

const gb: MemoryUnitProps = {
  number: Math.pow(1024, 3),
  label: 'Gb',
};

const mb: MemoryUnitProps = {
  number: Math.pow(1024, 2),
  label: 'Mb',
};

const kb: MemoryUnitProps = {
  number: Math.pow(1024, 1),
  label: 'Kb',
};

const bytes: MemoryUnitProps = {
  number: 1,
  label: 'bytes',
};

export const bytesConversion = (byte: number, toFixed = 2) => {
  if (byte === 0) return '0 bytes';

  const absByte = Math.abs(byte);
  const sign = byte < 0 ? -1 : 1;

  let size;

  if (absByte >= gb.number) {
    size = gb;
  } else if (absByte >= mb.number) {
    size = mb;
  } else if (absByte >= kb.number) {
    size = kb;
  } else {
    size = bytes;
  }

  const totalSize = ((sign * absByte) / size.number).toFixed(toFixed);
  return `${totalSize} ${size.label}`;
};

const roundTo = (value: number, precision: number): string => {
  const factor = Math.pow(10, precision);
  return (Math.round(value * factor) / factor).toFixed(precision);
};

export const showPercent = (value: number) => `${roundTo(value * 100, 2)}%`;
