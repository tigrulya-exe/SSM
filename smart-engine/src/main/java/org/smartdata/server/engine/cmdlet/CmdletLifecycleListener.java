package org.smartdata.server.engine.cmdlet;

public interface CmdletLifecycleListener {
  void cmdletAdded(long cmdletId);
  void cmdletStopped(long cmdletId);
  void cmdletDeleted(long cmdletId);
}
