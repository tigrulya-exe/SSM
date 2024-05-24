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
import { useEffect, useRef, useCallback, useMemo } from 'react';

/**
 * @param initialRequestProp - request, that is sent at  first time (usually it getEntitiesLit)
 * @param refreshRequestProp - request, that is sent at  first time (usually it getEntitiesLit)
 * @param requestFrequency
 * @param continueCondition - boolean condition, check before send every request
 * @param depsArray - list of dependencies
 */
export const useRequestTimer = (
  initialRequestProp: () => void,
  refreshRequestProp: () => void | null,
  requestFrequency: number, // in seconds
  continueCondition: boolean,
  depsArray: unknown[] = [],
) => {
  const timerRef = useRef<number | null>(null);

  const initialRequest = useMemo(
    () => (continueCondition ? initialRequestProp : null),
    [continueCondition, initialRequestProp],
  );
  const refreshRequest = useMemo(
    () => (!continueCondition ? null : refreshRequestProp ?? initialRequestProp),
    [continueCondition, refreshRequestProp, initialRequestProp],
  );

  const initTimer = useCallback(
    (isInitialRequestRequired: boolean) => {
      timerRef.current && clearInterval(timerRef.current);

      if (isInitialRequestRequired) {
        initialRequest?.();
      }

      if (requestFrequency && refreshRequest) {
        timerRef.current = window.setInterval(() => {
          refreshRequest();
        }, requestFrequency * 1000);
      }
    },
    [initialRequest, refreshRequest, requestFrequency],
  );

  useEffect(() => {
    initTimer(true);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, depsArray);

  useEffect(() => {
    initTimer(false);
  }, [initTimer]);

  useEffect(
    () => () => {
      timerRef.current && window.clearInterval(timerRef.current);
      timerRef.current = null;
    },
    [],
  );
};
