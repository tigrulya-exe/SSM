/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.cmdlet.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CmdletParserContext {

  public enum State {
    INSIDE_STR_LITERAL,
    INSIDE_TOKEN,
    EMPTY
  }

  private final char[] tokenBuffer;

  private final List<String> currentTokens;

  private final ParsedCmdlet.Builder cmdletBuilder;

  private int parseIndex;
  private int currentTokenLen;

  private State state;

  public CmdletParserContext(String cmdlet) {
    this.tokenBuffer = new char[cmdlet.length()];
    this.currentTokens = new ArrayList<>();
    this.parseIndex = 0;
    this.currentTokenLen = 0;
    this.state = State.EMPTY;
    this.cmdletBuilder = ParsedCmdlet.newBuilder()
        .setCmdletString(cmdlet);
  }

  public void addChar(char ch) {
    tokenBuffer[currentTokenLen++] = ch;
  }

  public void tokenEnded() {
    currentTokens.add(buildToken(tokenBuffer, currentTokenLen));
    currentTokenLen = 0;
    stateTransition(State.EMPTY);
  }

  public void addAction(String name, Map<String, String> args) {
    cmdletBuilder.addAction(new ParsedAction(name, args));
  }

  public void clearTokens() {
    currentTokens.clear();
  }

  public void stateTransition(State newState) {
    this.state = newState;
  }

  public List<String> getCurrentTokens() {
    return currentTokens;
  }

  public State getState() {
    return state;
  }

  public int getParseIndex() {
    return parseIndex;
  }

  public void setParseIndex(int parseIndex) {
    this.parseIndex = parseIndex;
  }

  public ParsedCmdlet buildParsedCmdlet() {
    return cmdletBuilder.build();
  }

  private String buildToken(char[] buffer, int len) {
    return String.valueOf(buffer, 0, len);
  }
}
