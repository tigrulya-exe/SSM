package org.smartdata.server.engine.action;

import org.smartdata.protocol.message.ActionStatus;

public interface ActionStatusUpdateListener {
  void onStatusUpdate(ActionStatus actionStatus) throws Exception;
}
