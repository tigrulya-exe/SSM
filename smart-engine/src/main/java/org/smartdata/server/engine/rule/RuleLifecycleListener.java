package org.smartdata.server.engine.rule;

public interface RuleLifecycleListener {
  void ruleAdded(long ruleId);
  void ruleStarted(long ruleId);
  void ruleStopped(long ruleId);
  void ruleDeleted(long ruleId);
}
