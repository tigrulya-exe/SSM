/**
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
package org.smartdata;

import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface LoadableService {
  static <T extends LoadableService> T load(
      Class<T> serviceClass, Supplier<T> defaultInstance) {
    ServiceLoader<T> serviceLoader = ServiceLoader.load(serviceClass);
    return StreamSupport.stream(serviceLoader.spliterator(), false)
        .findFirst()
        .orElseGet(defaultInstance);
  }

  static <T extends LoadableService> T loadOrThrow(Class<T> serviceClass) {
    return load(serviceClass, () -> {
      throw new IllegalStateException(
          "Instances of the class not found: " + serviceClass.getName());
    });
  }

  static <T extends LoadableService> Stream<T> loadAll(Class<T> serviceClass) {
    return StreamSupport.stream(ServiceLoader.load(serviceClass).spliterator(), false);
  }
}
