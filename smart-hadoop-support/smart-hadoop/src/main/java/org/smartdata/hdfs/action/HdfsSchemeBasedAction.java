package org.smartdata.hdfs.action;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class HdfsSchemeBasedAction extends HdfsAction {


  private final SchemeHandlerRegistry actionRegistry;


}
