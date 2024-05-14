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
package org.apache.zeppelin.notebook.repo.zeppelinhub.websocket.protocol;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.zeppelin.notebook.socket.Message.OP;
import org.junit.Test;

import com.google.common.collect.Maps;

public class ZeppelinhubMessageTest {
  
  private String msg = "{\"op\":\"LIST_NOTES\",\"data\":\"my data\",\"meta\":{\"key1\":\"val1\"}}";

  @Test
  public void testThatCanSerializeZeppelinHubMessage() {
    Map<String,String> meta = Maps.newHashMap();
    meta.put("key1", "val1");
    String zeppelinHubMsg = ZeppelinhubMessage.newMessage(OP.LIST_NOTES, "my data", meta).serialize();

    assertEquals(msg, zeppelinHubMsg);
  }
  
  @Test
  public void testThastCanDeserialiseZeppelinhubMessage() {
    Map<String,String> meta = Maps.newHashMap();
    meta.put("key1", "val1");
    ZeppelinhubMessage expected = ZeppelinhubMessage.newMessage(OP.LIST_NOTES.toString(), "my data", meta);
    ZeppelinhubMessage zeppelinHubMsg = ZeppelinhubMessage.deserialize(msg);

    assertEquals(expected.op, zeppelinHubMsg.op);
    assertEquals(expected.data, zeppelinHubMsg.data);
    assertEquals(expected.meta, zeppelinHubMsg.meta);
  }
  
  @Test
  public void testThatInvalidStringReturnEmptyZeppelinhubMessage() {
    assertEquals(ZeppelinhubMessage.EMPTY, ZeppelinhubMessage.deserialize(""));
    assertEquals(ZeppelinhubMessage.EMPTY, ZeppelinhubMessage.deserialize("dwfewewrewr"));
  }

}
