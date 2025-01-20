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
package org.smartdata.metastore.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smartdata.metastore.model.SearchResult;
import org.smartdata.metastore.queries.PageRequest;
import org.smartdata.metastore.queries.sort.SortField;

import java.io.IOException;
import java.util.List;

import static org.smartdata.metastore.utils.MetaStoreUtils.logAndBuildMetastoreException;

@RequiredArgsConstructor
@Slf4j
public class SearchableService<RequestT, EntityT, ColumnT extends SortField>
 implements Searchable<RequestT, EntityT, ColumnT> {

  private final Searchable<RequestT, EntityT, ColumnT> dbDelegate;
  private final String resourceName;

  @Override
  public SearchResult<EntityT> search(RequestT searchRequest, PageRequest<ColumnT> pageRequest)
      throws IOException {
    try {
      return dbDelegate.search(searchRequest, pageRequest);
    } catch (Exception exception) {
      throw logAndBuildMetastoreException(log, "Error searching " + resourceName, exception);
    }
  }

  @Override
  public List<EntityT> search(RequestT searchRequest) throws IOException {
    try {
      return dbDelegate.search(searchRequest);
    } catch (Exception exception) {
      throw logAndBuildMetastoreException(log, "Error searching " + resourceName, exception);
    }
  }
}
