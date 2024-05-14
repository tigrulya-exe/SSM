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
package org.apache.zeppelin.notebook.repo.zeppelinhub.websocket.mock;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockEventSocket extends WebSocketAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(MockEventServlet.class);
  private Session session;

  @Override
  public void onWebSocketConnect(Session session) {
    super.onWebSocketConnect(session);
    this.session = session;
    LOG.info("Socket Connected: " + session);
  }

  @Override
  public void onWebSocketText(String message) {
    super.onWebSocketText(message);
    session.getRemote().sendStringByFuture(message);
    LOG.info("Received TEXT message: {}", message);
    
  }

  @Override
  public void onWebSocketClose(int statusCode, String reason) {
    super.onWebSocketClose(statusCode, reason);
    LOG.info("Socket Closed: [{}] {}", statusCode, reason);
  }

  @Override
  public void onWebSocketError(Throwable cause) {
    super.onWebSocketError(cause);
    LOG.error("Websocket error: {}", cause);
  }
}