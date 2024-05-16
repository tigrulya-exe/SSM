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
package org.smartdata.client;

import org.smartdata.protocol.SmartClientProtocol;

import java.net.InetSocketAddress;
import java.util.Objects;

public class SmartServerHandle {
  private final SmartClientProtocol protocol;

  private final InetSocketAddress address;

  public SmartServerHandle(SmartClientProtocol protocol, InetSocketAddress address) {
    this.protocol = protocol;
    this.address = address;
  }

  public SmartClientProtocol getProtocol() {
    return protocol;
  }

  public InetSocketAddress getAddress() {
    return address;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SmartServerHandle that = (SmartServerHandle) o;
    return Objects.equals(address, that.address);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address);
  }

  @Override
  public String toString() {
    return "SmartServerHandle{"
        + "address=" + address
        + '}';
  }
}
